
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * @author Vignesh Thirunavukkarasu
 * @netid VXT200003
 * @email vignesh.thirunavukkarasu@utdallas.edu
 */
public class EFS extends Utility {

    public static final int AES_BLOCK_SIZE = 128;
    public static final int ENC_BLOCK_SIZE = 16;
    public static final int FILE_SIZE_BYTES = Config.BLOCK_SIZE - ENC_BLOCK_SIZE * 2;

    public EFS(Editor e) {
        super(e);
        set_username_password();
    }

    /**
     * Begin Helper functions
     */


    /**
     * Function to generate a score for the password
     * This value is used with file length to confuse the actual file size
     *
     * @param fileName
     * @return
     */
    public static int getScore(String fileName) {
        fileName = fileName.replaceAll("[^A-Za-z0-9]", "");
        int sum = 0;
        fileName = fileName.toLowerCase();

        for (int i = 0; i < fileName.length(); i++)
            sum += fileName.charAt(i) - 'a' + 1;
        return sum / fileName.length();
    }

    /**
     * Update the length of the file
     * Modify first line in metadata file (0th file)
     *
     * @param fileName - file name to be updated
     * @param length   - new length (old length will be removed)
     * @throws Exception
     */
    public void updateFileLength(String fileName, int length) throws Exception {
        String metaFile = fileName + File.separator + "0";

        String newLength = String.valueOf(length * getScore(fileName));
        String newLenBase64 = Base64.getEncoder().encodeToString(newLength.getBytes());
        String[] contents = new String(read_from_file(new File(metaFile))).split("\n");
        contents[0] = newLenBase64;
        byte[] iv = getIV(fileName);

        // remove the last null padding line
        contents = Arrays.copyOf(contents, contents.length - 1);

        StringBuilder updatedMeta = new StringBuilder();
        for (String s : contents) {
            updatedMeta.append(s).append("\n");
        }

        byte[] f = nullPadString(updatedMeta.toString(), FILE_SIZE_BYTES).getBytes();
        byte[] hmac = calculateHMAC(f, iv);

        byte[] signedFile = concatenateByteArrayList(Arrays.asList(f, hmac));
        System.out.println("Final Metadata Length : " + signedFile.length);
        save_to_file(signedFile, new File(fileName, "0"));
    }

    public byte[] splitBytes(byte[] array, int sp, int ep) {
        return Arrays.copyOfRange(array, sp, ep + 1);
    }

    /**
     * Increment the given iv by given number of bits
     *
     * @param iv
     * @param incrementBy
     */
    public void incrementIV(byte[] iv, int incrementBy) {

        // do not increment if increment by = 0;
        // it's for the start of the entire file (chunk 1, block 1)
        if (incrementBy == 0)
            return;

        int j = 0;
        while (j < incrementBy) {
            int i = iv.length - 1;
            while (true) {
                if (i == 0 && iv[i] == Byte.MAX_VALUE) {
                    iv[iv.length - 1] = Byte.MIN_VALUE;
                    break;
                } else if (iv[i] == Byte.MAX_VALUE) {
                    i--;
                } else {
                    iv[i] += 1;
                    break;
                }
            }
            j++;
        }
//        System.out.println(Arrays.toString(iv));
    }

    public byte[] trimByteArray(byte[] input, int size) {
        int i = 0;
        byte[] out = new byte[size];
        while (i < size) {
            out[i] = input[i];
            i++;
        }
        return out;
    }


    /**
     * Encrypt a block of 16 bytes (128 bits) with incremental IV (Counter Mode)
     * - Message is padded with null padding till the appropriate length is reached
     * - Padded message bytes are then split into 16 byte chunks and encrypted with IV
     * - IV is Bit incremented for each block
     * - Encrypted blocks are concatenated
     * - Since the encryption is AES/ECB/NOPADDING, input length will be equal to the output length
     *
     * @param message   - Input message bytes
     * @param iv        - Initial IV
     * @param blockSize - Block size for the split
     * @return - encrypted final blocks
     * @throws Exception
     */
    public byte[] blockEncrypt(byte[] message, byte[] iv, int blockSize) throws Exception {

        // Zero padding till message reaches a multiple of the block size
        // 16 for AES
//        byte[] paddedBytes = zeroPad(message, (int) Math.ceil((double) message.length / blockSize) * blockSize);byte[] paddedBytes = zeroPad(message, (int) Math.ceil((double) message.length / blockSize) * blockSize);
        byte[] paddedBytes = nullPadding(message, (int) Math.ceil((double) message.length / blockSize) * blockSize);

        /*int paddingLength = (int) Math.ceil((double) message.length / blockSize) * blockSize;
        byte[] paddedBytes;
        if (message.length == paddingLength)
            paddedBytes = message;
        else
            paddedBytes = nullPadding(message, (int) Math.ceil((double) message.length / blockSize) * blockSize);
        */
        byte[] outBytes = new byte[]{};

        System.out.println("Encryption Padded bytes : " + paddedBytes.length);
        int i = 0;
        while (i < paddedBytes.length) {
            byte[] block = splitBytes(paddedBytes, i, i + blockSize - 1);
            byte[] blockOut = encript_AES(block, iv);
            outBytes = concatenateByteArrayList(Arrays.asList(outBytes, blockOut));
            i += blockSize;
            incrementIV(iv, 1);
        }
        System.out.println("Encryption final out length " + outBytes.length);
        return outBytes;
    }

    public byte[] blockDecrypt(byte[] message, byte[] iv, int messageSize, int blockSize) throws Exception {

        byte[] outBytes = new byte[]{};

        System.out.println("Message length " + message.length);
        System.out.println("Message size : " + messageSize);
        int i = 0;

        // message length can be anything, so use message Size to decrypt it properly
        while (i < messageSize) {
            byte[] block = splitBytes(message, i, i + blockSize - 1);
            byte[] blockOut = decript_AES(block, iv);
            outBytes = concatenateByteArrayList(Arrays.asList(outBytes, blockOut));
            i += blockSize;
            incrementIV(iv, 1);
        }

        return trimByteArray(outBytes, messageSize);
    }

    public byte[] splitBytesWithSize(byte[] array, int sp, int len) {
        return Arrays.copyOfRange(array, sp, sp + len);
    }

    public boolean verifyPassword(String password, String file_name) throws Exception {
        System.out.println("Validating password");

        byte[] original_hash_bytes = splitBytes(Base64.getDecoder().decode(getMetaDataLine(file_name, 2))
                , 0, 47);
        byte[] line4_bytes = Base64.getDecoder().decode(getMetaDataLine(file_name, 3).substring(0, 127));
        byte[] salt_bytes = splitBytes(line4_bytes, 0, 15);
        byte[] new_password_hash = getPasswordHash(salt_bytes
                , password.getBytes(StandardCharsets.UTF_8));

        return Arrays.equals(original_hash_bytes, new_password_hash);
//        return true;
    }

    public static byte[] randomPadding(byte[] message, int padding) {
        byte[] rand = secureRandomNumber(padding);
        System.out.println(new String(Base64.getEncoder().encode(rand)));

        System.arraycopy(message, 0, rand, 0, message.length);

        return rand;
    }

    public byte[] getPasswordHash(byte[] salt, byte[] pass) throws Exception {
        byte[] paddedPass = concatenateByteArrayList(Arrays.asList(salt, pass));
        return hash_SHA384(paddedPass);
    }

    public byte[] getIV(String fileName) throws Exception {
        byte[] data = Base64.getDecoder().decode(getMetaDataLine(fileName, 3));
        return splitBytes(data, 16, 31);
    }

    public byte[] nullPadding(byte[] msg, int len) {
        StringBuilder sb = new StringBuilder(new String(msg));

        /*while(sb.length() < len){
            sb.append("\0");
        }

        return sb.toString().getBytes();*/

        // String length might be different from bytes length
        // Using bytes length to compare
        while (sb.toString().getBytes().length < len) {
            sb.append("\0");
        }

        return sb.toString().getBytes();
    }

    // \0 will not work properly if odd chars are added
    // will work perfectly for even char's
    public String nullPadString(String msg, int len) {
        return String.format("%-" + len + "s", msg);
    }

    public byte[] generateMetaFile(String file_name, String user_name, String password) throws Exception {

        StringBuilder sb = new StringBuilder();

        if (user_name.length() > 128)
            throw new Exception("Username length > 128 bytes");
        else if (password.length() > 128)
            throw new Exception("Password length > 128 bytes");

        // 16 bytes = 128 bits
        byte[] saltBytes = secureRandomNumber(16);
        byte[] ivBytes = secureRandomNumber(16);

        byte[] passBytes = password.getBytes(StandardCharsets.UTF_8);
        byte[] hashedPass = getPasswordHash(saltBytes, passBytes);

//        byte[] lenEnc = encrypt_AES("0".getBytes(), ivBytes);
        // file length
        String line1 = Base64.getEncoder().encodeToString("0".getBytes());
        // user_name
        String line2 = Base64.getEncoder().encodeToString(user_name.getBytes());
        // hashed password
        String line3 = Base64.getEncoder().encodeToString(randomPadding(hashedPass, AES_BLOCK_SIZE));
        // salt + IV
        String line4 = Base64.getEncoder().encodeToString(randomPadding(concatenateByteArrayList(Arrays.asList(saltBytes, ivBytes)), AES_BLOCK_SIZE * 2));

//        String line5 = Base64.getEncoder().encodeToString(randomPadding("01234567899876543210123456789012".getBytes(), AES_BLOCK_SIZE));

        sb.append(line1).append(System.lineSeparator())
                .append(line2).append(System.lineSeparator())
                .append(line3).append(System.lineSeparator())
                .append(line4).append(System.lineSeparator());


        byte[] hmac = calculateHMAC(sb.toString().getBytes(), ivBytes);
        sb.append(new String(hmac));
        // null padding till file size = 1024
        System.out.println("Metafile size before padding : " + sb.length());
        byte[] padded_meta_file = nullPadding(sb.toString().getBytes(), Config.BLOCK_SIZE);

        System.out.println("Metafile bytes size after padding : " + padded_meta_file.length);

        // write to file
//        save_to_file(padded_meta_file, new File(file_name, "0"));

        return padded_meta_file;
    }

    public String getMetaDataLine(String file, int lineNo) throws Exception {
        byte[] contentsBytes = read_from_file(new File(file, "0"));
        String[] split = new String(contentsBytes, StandardCharsets.UTF_8).split(System.lineSeparator());
        return split[lineNo];
    }

    public byte[] concatenateByteArrayList(List<byte[]> b1) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (byte[] bytes : b1) {
            outputStream.write(bytes);
        }
        return outputStream.toByteArray();
    }

    /**
     * Function used to delete the chunk files
     * used by cut() and write() function
     *
     * @param file_name  - file name
     * @param startBlock - starting block to delete
     * @param endBlock   - ending block to delete
     */
    public void removeChunkFiles(String file_name, int startBlock, int endBlock) {
        while (startBlock <= endBlock)
            new File(file_name, String.valueOf(startBlock++)).delete();
    }


    public static byte[] byteXOR(byte[] key, byte[] pad) {
        byte[] out = new byte[key.length];
        for (int i = 0; i < key.length; i++)
            out[i] = (byte) (key[i] ^ pad[i % pad.length]);

        return out;
    }


    /**
     * HMAC Calculation Algorithm
     * - If key size > 64 bytes, HASH it, else Zero Pad it to 64 bytes
     * - Generate ipad-key by XOR of key and ipad byte (0x36) repeated till key's length
     * - Generate opad-key by XOR of key and opad byte (5c) repeated till key's length
     * - Hash 1 - Hash of ipadKey and message
     * - Hash 2 - Hash of opadKey and Hash 1
     * - Hash 2 is the HMAC of the message
     *
     * @param message - encrypted message bytes
     * @param key     - valid key
     * @return
     * @throws Exception
     */
    public byte[] calculateHMAC(byte[] message, byte[] key) throws Exception {

        key = key.length > 64 ? hash_SHA256(key) : nullPadding(key, 64);

        byte[] ipad = new byte[]{0x36};
        byte[] opad = new byte[]{0x5c};

        byte[] ipadKey = byteXOR(key, ipad);
        byte[] opadKey = byteXOR(key, opad);

        byte[] firstHash = Utility.hash_SHA256(concatenateByteArrayList(Arrays.asList(ipadKey, message)));

        return Utility.hash_SHA256(concatenateByteArrayList(Arrays.asList(opadKey, firstHash)));
    }

    // returns the greatest number multiple of roundToMultiple
    public int roundNumber(int number, int roundToMultiple) {

        int remainder = number % roundToMultiple;
        if (remainder == 0)
            return number;
        else
            return number + (roundToMultiple - remainder);
    }


    /**
     * End Helper functions
     */


    /**
     * Steps to consider... <p>
     * - add padded username and password salt to header <p>
     * - add password hash and file length to secret data <p>
     * - AES encrypt padded secret data <p>
     * - add header and encrypted secret data to metadata <p>
     * - compute HMAC for integrity check of metadata <p>
     * - add metadata and HMAC to metadata file block <p>
     */
    @Override
    public void create(String file_name, String user_name, String password) throws Exception {
        File dir = new File(file_name);
        if (dir.exists() && dir.isDirectory()) {
            throw new Exception("File already exists");
        }
        dir.mkdirs();
        System.out.println("Directory Created");
        File meta_file = new File(file_name, "0");
        save_to_file(generateMetaFile(file_name, user_name, password), meta_file);
        System.out.println("Metadata file written successfully");
    }

    /**
     * Steps to consider... <p>
     * - check if metadata file size is valid <p>
     * - get username from metadata <p>
     * Read contents of 0th child file of the given file
     * Base64 decode 2nd line (1)
     */
    @Override
    public String findUser(String file_name) throws Exception {
        File dir = new File(file_name);
        String user_name = "";
        if (dir.exists() && dir.isDirectory()) {
            user_name = getMetaDataLine(file_name, 1);
        } else {
            throw new Exception("Invalid Directory / File provided");
        }
        return new String(Base64.getDecoder().decode(user_name)).trim();
    }

    /**
     * Steps to consider...:<p>
     * - get password, salt then AES key <p>
     * - decrypt password hash out of encrypted secret data <p>
     * - check the equality of the two password hash values <p>
     * - decrypt file length out of encrypted secret data
     */
    @Override
    public int length(String file_name, String password) throws Exception {
        File dir = new File(file_name);
        String length = "";
        if (verifyPassword(password, file_name)) {
            if (dir.exists() && dir.isDirectory()) {
                length = new String(Base64.getDecoder().decode(getMetaDataLine(file_name, 0))
                        , StandardCharsets.UTF_8);
                return Integer.parseInt(length) / getScore(file_name);
            }
        } else
            throw new PasswordIncorrectException();

        return 0;
    }

    @Override
    public byte[] read(String file_name, int starting_position, int len, String password) throws Exception {
//        return read_new(file_name, starting_position, len, password);
        return read2(file_name, starting_position, len, password);
    }

    public byte[] read2(String file_name, int starting_position, int len, String password) throws Exception {

        if (!verifyPassword(password, file_name))
            throw new PasswordIncorrectException();

        File root = new File(file_name);
        int file_length = length(file_name, password);
        System.out.println("File length : " + file_length);
        if (starting_position + len > file_length) {
            throw new Exception("Overflow, Can not read starting_pos : " + starting_position + ", len : " + len + ", file_length : " + file_length);
        }

        int ending_position = starting_position + len -1;
        int startFileBlock = starting_position / FILE_SIZE_BYTES;
        // adjusting end block to have accuracy
        int endFileBlock = ending_position / FILE_SIZE_BYTES;

        System.out.println("Start block : " + startFileBlock + ", endFileBlock : " + endFileBlock);

        byte[] iv = getIV(file_name);

        byte[] ivDec = new byte[iv.length];
        System.arraycopy(iv, 0, ivDec, 0, iv.length - 1);

        // increment IV to the correct pointer
        int incrementBy = startFileBlock * 62;
        System.out.println("IV Increment by " + incrementBy);
        incrementIV(ivDec, incrementBy);

        String returnString = "";
        String encString = "";

        for (int i = startFileBlock + 1; i <= endFileBlock + 1; i++) {
            byte[] contents = read_from_file(new File(root, Integer.toString(i)));
            // getting only data string, bytes [992, 1023] are HMAC data
            String data = new String(contents, "ISO-8859-1").substring(0, 992);
            encString += data;
        }

        int sp = starting_position % 16;
        int ep = sp + len;

        int startAESBlockReference = (int) Math.floor((double) sp / 16);
        int endAESBlockInReference = (int) Math.ceil((double) ep / 16);

        System.out.println("Reference Start block " + startAESBlockReference + ", End block : " + endAESBlockInReference);
        encString = encString.substring(startAESBlockReference*16, endAESBlockInReference*16);

        System.out.println("Length of string to be decrypted : " + encString.length());
        returnString = new String(blockDecrypt(encString.getBytes("ISO-8859-1"), ivDec, encString.length(), 16), "ISO-8859-1");

//        System.out.println("Decrypted string : " + returnString);
        System.out.println("Splitting decrypted string from : " + sp + ", to : " + ep);

        return returnString.substring(sp, ep).getBytes();
    }

    /*public byte[] read_new(String file_name, int starting_position, int len, String password) throws Exception {

        if (!verifyPassword(password, file_name)) {
            throw new PasswordIncorrectException();
        }

        File root = new File(file_name);
        int file_length = length(file_name, password);
        System.out.println("File length : " + file_length);
        if (starting_position + len > file_length) {
            throw new Exception("Overflow, Can not read starting_pos : " + starting_position + ", len : " + len + ", file_length : " + file_length);
        }

        int start_block = starting_position / FILE_SIZE_BYTES;
        // adjusting end block to have accuracy
        int end_block = (starting_position + len - 1) / FILE_SIZE_BYTES;

        System.out.println("Start block : " + start_block + ", end_block : " + end_block);

        byte[] iv = getIV(file_name);

        byte[] ivDec = new byte[iv.length];
        System.arraycopy(iv, 0, ivDec, 0, iv.length - 1);

        // increment IV to the correct pointer
        int incrementBy = start_block * 62;
        System.out.println("IV Increment by " + incrementBy);
        incrementIV(ivDec, incrementBy);

        byte[] toReturn = "".getBytes();

        String returnString = "";

        int lengthToDecrypt = 0;
        for (int i = start_block + 1; i <= end_block + 1; i++) {
            byte[] temp = read_from_file(new File(root, Integer.toString(i)));

            temp = splitBytesWithSize(temp, 0, FILE_SIZE_BYTES);
            System.out.println("Reading file " + i + ", Read length : " + temp.length);
            byte[] d = blockDecrypt(temp, ivDec, FILE_SIZE_BYTES, ENC_BLOCK_SIZE);

            toReturn = concatenateByteArrayList(Arrays.asList(toReturn, temp));
            returnString += new String(d);
            lengthToDecrypt += FILE_SIZE_BYTES;
        }

        System.out.println("Length to decrypt : " + lengthToDecrypt);

        int sp = starting_position < FILE_SIZE_BYTES ? starting_position : starting_position - start_block * FILE_SIZE_BYTES;

        int ep = starting_position + len - (start_block) * FILE_SIZE_BYTES;
        ep = Math.max(0, ep);

        return returnString.substring(sp, ep).getBytes();
    }*/

    public void write_new(String file_name, int starting_position, byte[] content, String password) throws Exception {
        System.out.println("======================================================");

        if (!verifyPassword(password, file_name)) {
            throw new PasswordIncorrectException();
        }

        int file_length = length(file_name, password);
        System.out.println("File length " + file_length);

        int len = content.length;

        if (starting_position > file_length)
            throw new Exception("Starting pos > file length");

        if (file_length == 0) {
            System.out.println("No contents exists in the file. Creating File chunk 1");
            new File(file_name, "1").createNewFile();
        }

        int ending_position = starting_position + len - 1;

        int startFileBlock = starting_position / 992;
        int endFileBlock = ending_position / FILE_SIZE_BYTES;

        int prefixStartPosition = startFileBlock * 992;
        int prefixLength = Math.min(992, file_length - startFileBlock * 992);
        int prefixEndPosition = prefixStartPosition + prefixLength;

        int suffixStartPosition = endFileBlock * 992;
        int suffixLength = Math.min(992, file_length - endFileBlock * 992);
        int suffixEndPosition = suffixStartPosition + suffixLength;

        System.out.println("starting_position : " + starting_position + ", length : " + len);
        System.out.println("Prefix : " + prefixStartPosition + ", " + prefixEndPosition);
        System.out.println("Suffix : " + suffixStartPosition + ", " + suffixEndPosition);


        byte[] suffixBlockContents, prefixBlockContents;

        if ( file_length == 0 ){
            suffixBlockContents = "".getBytes("ISO-8859-1");
            prefixBlockContents = "".getBytes("ISO-8859-1");
        }else{
            suffixBlockContents = read(file_name, suffixStartPosition, suffixLength, password);
            prefixBlockContents = read(file_name, prefixStartPosition, prefixLength, password);
        }


        String prefixString = new String(prefixBlockContents, "ISO-8859-1");
        String suffixString = new String(suffixBlockContents, "ISO-8859-1");

        System.out.println("Prefix Block length : " + prefixString.length());
        System.out.println("Suffix Block length : " + suffixString.length());

        if (starting_position + len > file_length) {
            prefixString = prefixString.substring(0, starting_position % 992);
            suffixString = "";
        } else {
            prefixString = prefixString.substring(0, starting_position % 992);
            suffixString = suffixString.substring((ending_position + 1) % 992);
        }

        System.out.println("Prefix string : " + prefixString);
        System.out.println("Prefix string length : " + prefixString.length());

        System.out.println("Suffix string : " + suffixString);
        System.out.println("Suffix string length : " + suffixString.length());

        String finalString = prefixString + new String(content, "ISO-8859-1") + suffixString;

        System.out.println("Final String : " + finalString);
        System.out.println("Final string length : " + finalString.length());
        System.out.println("Final byte length : " + finalString.getBytes("ISO-8859-1").length);

        int updatedLength = finalString.length();
        byte[] iv = getIV(file_name);

        byte[] ivEnc = new byte[iv.length];
        System.arraycopy(iv, 0, ivEnc, 0, iv.length - 1);

        int incrementIvBy = startFileBlock * 62;
        System.out.println("Incrementing iv by : " + incrementIvBy);
        incrementIV(ivEnc, incrementIvBy);

        System.out.println("Updated length before padding " + updatedLength);
        int toPadLength = roundNumber(updatedLength, FILE_SIZE_BYTES);

        System.out.println("Null padding to Length : " + toPadLength);
        finalString = nullPadString(finalString, toPadLength);

        System.out.println("After Padding string length : " + finalString.length());
        System.out.println("After Padding bytes length : " + finalString.getBytes("ISO-8859-1").length);

        int finalLength = starting_position + len;
        if (finalLength < file_length)
            finalLength = file_length;

        int i = 0;
        for (int j = startFileBlock + 1; j <= endFileBlock + 1 && i + FILE_SIZE_BYTES <= finalString.length(); j++) {
            byte[] chunk = finalString.substring(i, i + FILE_SIZE_BYTES).getBytes("ISO-8859-1");
            byte[] encChunk = blockEncrypt(chunk, ivEnc, ENC_BLOCK_SIZE);
            System.out.println("Length after enc : " + encChunk.length);
            byte[] hmac = calculateHMAC(encChunk, iv);
            byte[] encSignedBytes = concatenateByteArrayList(Arrays.asList(encChunk, hmac));
            System.out.println("Length of chunk " + j + " after signed : " + encSignedBytes.length);
            save_to_file(encSignedBytes, new File(file_name, String.valueOf(j)));
            i = i + FILE_SIZE_BYTES;
        }

        if (finalLength > file_length) {
            System.out.println("Overflow, updating file length : " + finalLength);
            updateFileLength(file_name, finalLength);
        }

        System.out.println("File written successfully");

        System.out.println("======================================================");
    }

    /**
     * Algorithm:
     * - Read Metadata file for existing file length, iv
     * - Use read() method, starting_position and file_length to get the contents of the file
     * - Read till end of the file, as all the contents will be affected because of inserting text
     * - Read method will by default give the decrypted text
     * - starting_position will be adjusted to that entire block itself
     * - split the text with the starting_position % BLOCK_SIZE to get the position in that block (prefix & suffix)
     * - Append the new text between the split text
     * - Append the remaining text till length reaches multiples of 1024 (BLOCK_SIZE)
     * - Encrypt the text with the IV from metadata file
     * - Remove the file parts that were read
     * - Write the new encrypted text into files (Chunks of 1024 bytes into each file)
     *
     * @param file_name         - file to write
     * @param starting_position - start position to write
     * @param content           - contents to write from starting_position
     * @param password          - password for the file
     * @throws Exception
     */
    @Override
    public void write(String file_name, int starting_position, byte[] content, String password) throws Exception {

        /*if (!verifyPassword(password, file_name)) {
            throw new PasswordIncorrectException();
        }

        int file_length = length(file_name, password);
        System.out.println("File length " + file_length);

        int len = content.length;

        if (starting_position > file_length)
            throw new Exception("Starting pos > file length");

        starting_position = Math.max(starting_position - 1, 0);

        int ending_position = starting_position + len - 1;
        int startFileBlock = starting_position / FILE_SIZE_BYTES;

        int endFileBlock = roundNumber(ending_position, FILE_SIZE_BYTES) / FILE_SIZE_BYTES;


        if (file_length == 0) {
            System.out.println("No contents exists in the file. Creating File chunk 1");
            new File(file_name, "1").createNewFile();
        }

        // start and end block to be read, the complete block files will be read
        // if the readTill is greater than the file length, then till end of the file is read
        // else, read() function might throw Error
        int readFrom = startFileBlock * FILE_SIZE_BYTES;
        int readTill = Math.min(endFileBlock * FILE_SIZE_BYTES, file_length);
        int readLength = readTill - readFrom;

        byte[] iv = getIV(file_name);

        byte[] ivEnc = new byte[iv.length];
        System.arraycopy(iv, 0, ivEnc, 0, iv.length - 1);

        int incrementIvBy = readFrom / ENC_BLOCK_SIZE;
        System.out.println("Incrementing iv by : " + incrementIvBy);
        incrementIV(ivEnc, incrementIvBy);

        // For a given starting_position X, sp has to end till X-1 position.
        int sp = Math.max(starting_position % FILE_SIZE_BYTES, 0);
        int ep = sp + len;


        System.out.println("SP : " + sp + ", EP : " + ep);
        System.out.println("Read from : " + readFrom + ", Read Till : " + readTill + ", Read Length: " + readLength);

        byte[] decContents = read_new(file_name, readFrom, readLength, password);
        String readString = new String(decContents);


        String prefix = readString.substring(0, sp);
        String suffix = "";
        // condition for overflow
        if (starting_position + len < file_length)
            suffix = readString.substring(ep);

        String updatedString = prefix + new String(content) + suffix;

        if (updatedString.equals(readString)) {
            System.out.println("To write content same as existing content. Ignoring write operation");
            return;
        }

        System.out.println("Updated String length : " + updatedString.length());
        byte[] updatedBytes = updatedString.getBytes();
        int updatedLength = updatedBytes.length;

        System.out.println("Updated length before padding " + updatedLength);
        int toPadLength = roundNumber(updatedLength, FILE_SIZE_BYTES);

        System.out.println("Null padding to Length : " + toPadLength);
        updatedString = nullPadString(updatedString, toPadLength);
        updatedBytes = updatedString.getBytes();
        System.out.println("Updated string after padding : " + updatedString);
        updatedLength = updatedBytes.length;
        System.out.println("Final padded bytes length : " + updatedLength);
        System.out.println("Final padded string length : " + updatedString.length());


        System.out.println("Before writing files");

        int finalLength = starting_position + len;
        if (finalLength < file_length)
            finalLength = file_length;

        int i = 0;
        for (int j = startFileBlock + 1; j <= endFileBlock + 1 && i + FILE_SIZE_BYTES <= updatedBytes.length; j++) {
            byte[] chunk = updatedString.substring(i, i + FILE_SIZE_BYTES).getBytes();
            byte[] encChunk = blockEncrypt(chunk, ivEnc, ENC_BLOCK_SIZE);
            System.out.println("Length after enc : " + encChunk.length);
            byte[] hmac = calculateHMAC(encChunk, iv);
            byte[] encSignedBytes = concatenateByteArrayList(Arrays.asList(encChunk, hmac));
            System.out.println("Length of chunk " + j + " after signed : " + encSignedBytes.length);
            save_to_file(encSignedBytes, new File(file_name, String.valueOf(j)));
            i = i + FILE_SIZE_BYTES;
        }

        if (finalLength > file_length) {
            System.out.println("Overflow, updating file length");
            updateFileLength(file_name, finalLength);
        }

        System.out.println("File written successfully");*/
        write_new(file_name, starting_position, content, password);
    }

    /**
     * Steps to consider...:<p>
     * - verify password
     * - Find the length and the total file blocks available
     * - Each file, last 32 bytes are the HMAC, read each file and get the contents of them
     * - Calculate the HMAC of the remaining 992 bytes (1024 - 32) and check if they are equal to the pre-existing HMAC
     * - If any of the file doesn't match, return false
     * - Check also includes metadata file as well.
     */
    @Override
    public boolean check_integrity(String file_name, String password) throws Exception {

        int status = 0;

        if (!verifyPassword(password, file_name)) {
            throw new PasswordIncorrectException();
        }

        int length = length(file_name, password);
        byte[] iv = getIV(file_name);
        int totalFiles = (int) Math.ceil((double) length / FILE_SIZE_BYTES);
        System.out.println("INTEGRITY : Total files : " + totalFiles);

        for (int i = 0; i <= totalFiles; i++) {
            String f = file_name + File.separator + i;
            try {
                byte[] contents = read_from_file(new File(f));
                byte[] encMsg = splitBytes(contents, 0, 991);
                String existingHMAC = new String(splitBytes(contents, 992, Config.BLOCK_SIZE));
                String calculatedHMAC = new String(calculateHMAC(encMsg, iv));
                if (calculatedHMAC.equals(existingHMAC)) {
                    System.out.println("INTEGRITY : Chunk " + f + ", status : invalid");
                    status++;
                }
            } catch (Exception e) {
                status += 1;
                System.out.println("INTEGRITY : Exception occurred at processing file : " + f);
                System.out.println(e.getMessage());
            }
        }

        return status == 0;
    }

    /**
     * Steps to consider... <p>
     * - verify password <p>
     * - truncate the content after the specified newLength <p>
     * - re-pad, update metadata and HMAC <p>
     */
    @Override
    public void cut(String file_name, int len, String password) throws Exception {

        if (!verifyPassword(password, file_name)) {
            throw new PasswordIncorrectException();
        }

        File root = new File(file_name);
        int file_length = length(file_name, password);

        if (len > file_length) {
            throw new Exception("Length too long than contents of the file");
        } else if (len == file_length) {
            System.out.println("Same length. ignoring any operation.");
            return;
        }

        int end_block = (len) / FILE_SIZE_BYTES;

        byte[] iv = getIV(file_name);

        byte[] ivDec = new byte[iv.length];
        System.arraycopy(iv, 0, ivDec, 0, iv.length - 1);

        byte[] ivEnc = new byte[iv.length];
        System.arraycopy(iv, 0, ivEnc, 0, iv.length - 1);

        File file = new File(root, Integer.toString(end_block + 1));

        // decrypt the file and save as string
//        byte[] dec = decrypt_AES(read_from_file(file), iv);
        byte[] msg = read_from_file(file);
        byte[] dec = blockDecrypt(msg, ivDec, FILE_SIZE_BYTES, ENC_BLOCK_SIZE);
        String str = new String(dec);

        str = str.substring(0, len - end_block * FILE_SIZE_BYTES);
        while (str.length() < FILE_SIZE_BYTES) {
            str += '\0';
        }

        // re-encrypt the stripped contents and save to file
//        byte[] enc = encrypt_AES(str.getBytes(), ivEnc);
        byte[] enc = blockEncrypt(str.getBytes(), ivEnc, ENC_BLOCK_SIZE);
        byte[] hmac = calculateHMAC(enc, iv);
        byte[] signedEncBytes = concatenateByteArrayList(Arrays.asList(enc, hmac));
        save_to_file(signedEncBytes, file);

        int cur = end_block + 2;
        file = new File(root, Integer.toString(cur));
        while (file.exists()) {
            System.out.println("Deleting file : " + file.getAbsolutePath() + ", status : " + file.delete());
            cur++;
        }

        updateFileLength(file_name, len);
    }

}
