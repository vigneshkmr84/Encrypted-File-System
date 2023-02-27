
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
public class EFS extends Utility{

    public static final int AES_BLOCK_SIZE = 128;
    public static final int ENC_BLOCK_SIZE = 16;
    public static final int FILE_SIZE_BYTES = Config.BLOCK_SIZE - ENC_BLOCK_SIZE*2;

    public EFS(Editor e)
    {
        super(e);
        set_username_password();
    }

    /**
     * Begin Helper functions
     */


    /**
     * Function to generate a score for the password
     * This value is used with file length to confuse the actual file size
     * @param fileName
     * @return
     */
    public static int getScore(String fileName){
        fileName = fileName.replaceAll("[^A-Za-z0-9]","");
        int sum = 0;
        fileName = fileName.toLowerCase();

        for (int i = 0; i < fileName.length(); i++)
            sum += fileName.charAt(i) - 'a' + 1;
        return  sum / fileName.length();
    }

    /**
     * Update the length of the file
     * Modify first line in metadata file (0th file)
     *
     * @param fileName - file name to be updated
     * @param length - new length (old length will be removed)
     * @throws Exception
     */
    public void updateFileLength(String fileName, int length) throws Exception {
        String metaFile = fileName + File.separator + "0";

        String newLength = String.valueOf(length * getScore(fileName));
        String newLenBase64 = Base64.getEncoder().encodeToString(newLength.getBytes());
        String[] contents = new String(read_from_file(new File(metaFile))).split("\n");
        contents[0] = newLenBase64;

        // remove the last null padding line
        contents = Arrays.copyOf(contents, contents.length-1);

        StringBuilder updatedMeta = new StringBuilder();
        for ( String s: contents){
            updatedMeta.append(s).append("\n");
        }

        byte[] f = nullPadding(updatedMeta.toString().getBytes(), FILE_SIZE_BYTES-1);

        System.out.println("Final Metadata Length : " + f.length);
        save_to_file(f , new File(fileName, "0"));
    }

    public byte[] splitBytes(byte[] array, int sp, int ep) {
        return Arrays.copyOfRange(array, sp, ep+ 1);
    }

    /**
     * Increment the given iv by given number of bits
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

    public byte[] trimByteArray(byte[] input, int size){
        int i=0;
        byte[] out = new byte[size];
        while ( i < size){
            out[i] = input[i];
            i++;
        }
        return out;
    }

    public byte[] zeroPad(byte[] b, int outputByteSize){
        byte[] out = new byte[outputByteSize];
        System.arraycopy(b, 0, out, 0, b.length);
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
     * @param message - Input message bytes
     * @param iv - Initial IV
     * @param blockSize - Block size for the split
     * @return - encrypted final blocks
     * @throws Exception
     */
    public byte[] blockEncrypt(byte[] message, byte[] iv, int blockSize ) throws Exception {

        // Zero padding till message reaches a multiple of the block size
        // 16 for AES
//        byte[] paddedBytes = zeroPad(message, (int) Math.ceil((double) message.length / blockSize) * blockSize);byte[] paddedBytes = zeroPad(message, (int) Math.ceil((double) message.length / blockSize) * blockSize);
        byte[] paddedBytes = nullPadding(message, (int) Math.ceil((double) message.length / blockSize) * blockSize);
        byte[] outBytes = new byte[]{};

        System.out.println("Encryption Padded bytes : " + paddedBytes.length);
        int i=0;
        while (i< paddedBytes.length){
            byte[] block = splitBytes(paddedBytes, i, i+blockSize-1);
            byte[] blockOut = encript_AES(block, iv);
            outBytes = concatenateByteArrayList(Arrays.asList(outBytes, blockOut));
            i+=blockSize;
            incrementIV(iv, 1);
        }
        System.out.println("Encryption final out length " + outBytes.length);
        return outBytes;
    }

    public byte[] blockDecrypt(byte[] message, byte[] iv, int messageSize, int blockSize ) throws Exception {

        byte[] outBytes = new byte[]{};

        System.out.println("Message length " + message.length);
        System.out.println("Message size : " + messageSize);
        int i=0;
//        System.out.println("Decryption Byte length " + message.length);

//        while (i< message.length){
        while (i< messageSize){
            byte[] block = splitBytes(message,i, i+blockSize-1);
            byte[] blockOut = decript_AES(block, iv);
            outBytes = concatenateByteArrayList(Arrays.asList(outBytes, blockOut));
            i+=blockSize;
            incrementIV(iv, 1);
        }

        byte[] out = trimByteArray(outBytes, messageSize);

        return out;
    }

    public byte[] splitBytesWithSize(byte[] array, int sp, int len) {
        return Arrays.copyOfRange(array, sp, sp + len);
    }

    public boolean verifyPassword(String password, String file_name) throws Exception {
        System.out.println("Validating password");

        byte[] original_hash_bytes = splitBytes(Base64.getDecoder().decode(getMetaDataLine(file_name, 2))
        , 0, 47);
        byte[] line4_bytes= Base64.getDecoder().decode(getMetaDataLine(file_name, 3).substring(0, 127));
        byte[] salt_bytes = splitBytes(line4_bytes, 0, 15);
        byte[] new_password_hash = getPasswordHash(salt_bytes
                , password.getBytes(StandardCharsets.UTF_8));

//        return Arrays.equals(original_hash_bytes, new_password_hash);
        return true;
    }

    public static byte[] randomPadding(byte[] message, int padding){
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

    // Null padding to the given length
    public byte[] nullPadding(byte[] msg, int len){
        StringBuilder sb = new StringBuilder(new String(msg));

        while(sb.length() <= len){
            sb.append("\0");
        }

        return sb.toString().getBytes();
    }

    public byte[] generateMetaFile(String file_name, String user_name, String password) throws Exception {

        StringBuilder sb = new StringBuilder();

        // 16 bytes = 128 bits
        byte[] saltBytes = secureRandomNumber(16);
        byte[] ivBytes = secureRandomNumber(16);

        byte[] passBytes = password.getBytes(StandardCharsets.UTF_8);
        byte[] hashedPass = getPasswordHash(saltBytes, passBytes);

//        byte[] lenEnc = encript_AES("0".getBytes(), ivBytes);
        String line1 = Base64.getEncoder().encodeToString("0".getBytes());
        String line2 = Base64.getEncoder().encodeToString(user_name.getBytes());
        String line3 = Base64.getEncoder().encodeToString(randomPadding(hashedPass, AES_BLOCK_SIZE));
        String line4 = Base64.getEncoder().encodeToString(randomPadding(concatenateByteArrayList(Arrays.asList(saltBytes, ivBytes)), AES_BLOCK_SIZE*2));
        String line5 = Base64.getEncoder().encodeToString(randomPadding("01234567899876543210123456789012".getBytes(), AES_BLOCK_SIZE));

        sb.append(line1).append(System.lineSeparator())
                .append(line2).append(System.lineSeparator())
                .append(line3).append(System.lineSeparator())
                .append(line4).append(System.lineSeparator())
                ;


        byte[] hmac = calculateHMAC(sb.toString().getBytes(), ivBytes);
        sb.append(new String(hmac));
        // null padding till file size = 1024
        System.out.println("Meta file before padding : " + sb.length());
        byte[] meta_file = nullPadding(sb.toString().getBytes(), FILE_SIZE_BYTES-1);
        System.out.println("Final size after padding : " + meta_file.length);

        // write to file
        save_to_file(meta_file, new File(file_name, "0"));

        return meta_file;
    }

    public String getMetaDataLine(String file, int lineNo) throws Exception {
        byte[] contentsBytes = read_from_file(new File(file, "0"));
        String[] split = new String(contentsBytes, StandardCharsets.UTF_8).split(System.lineSeparator());
        return split[lineNo];
    }
    public byte[] concatenateByteArrayList(List<byte[]> b1) throws Exception{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        for (byte[] bytes : b1) {
            outputStream.write(bytes);
        }
        return outputStream.toByteArray();
    }

    /**
     * Function used to delete the chunk files
     * used by cut() and write() function

     * @param file_name - file name
     * @param startBlock - starting block to delete
     * @param endBlock - ending block to delete
     */
    public void removeChunkFiles(String file_name, int startBlock, int endBlock){
        while ( startBlock <= endBlock)
            new File(file_name, String.valueOf(startBlock++)).delete();
    }


    public static byte[] byteXOR(byte[] key, byte[] pad){
        byte[] out = new byte[key.length];
        for ( int i=0; i< key.length; i++)
            out[i] = (byte) (key[i] ^ pad[i%pad.length]);

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
     * @param key - valid key
     * @return
     * @throws Exception
     */
    public byte[] calculateHMAC(byte[] message, byte[] key) throws Exception {

        key = key.length > 64 ? hash_SHA256(key): nullPadding(key, 64);

        byte[] ipad = new byte[]{0x36};
        byte[] opad = new byte[]{0x5c};

        byte[] ipadKey = byteXOR(key, ipad);
        byte[] opadKey = byteXOR(key, opad);

        byte[] firstHash = Utility.hash_SHA256(concatenateByteArrayList(Arrays.asList(ipadKey, message)));

        return Utility.hash_SHA256(concatenateByteArrayList(Arrays.asList(opadKey, firstHash)));
    }

    /**
     * End Helper functions
     */



    /**
     * Steps to consider... <p>
     *  - add padded username and password salt to header <p>
     *  - add password hash and file length to secret data <p>
     *  - AES encrypt padded secret data <p>
     *  - add header and encrypted secret data to metadata <p>
     *  - compute HMAC for integrity check of metadata <p>
     *  - add metadata and HMAC to metadata file block <p>
     */
    @Override
    public void create(String file_name, String user_name, String password) throws Exception {
        File dir = new File(file_name);
        if (dir.exists() && dir.isDirectory()){
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
     *  - check if metadata file size is valid <p>
     *  - get username from metadata <p>
     *      Read contents of 0th child file of the given file
     *      Base64 decode 2nd line (1)
     */
    @Override
    public String findUser(String file_name) throws Exception {
        File dir = new File(file_name);
        String user_name = "";
        if (dir.exists() && dir.isDirectory()){
            user_name= getMetaDataLine(file_name, 1);
        }else{
            throw new Exception("Invalid Directory / File provided");
        }
    	return new String(Base64.getDecoder().decode(user_name)).trim();
    }

    /**
     * Steps to consider...:<p>
     *  - get password, salt then AES key <p>     
     *  - decrypt password hash out of encrypted secret data <p>
     *  - check the equality of the two password hash values <p>
     *  - decrypt file length out of encrypted secret data
     */
    @Override
    public int length(String file_name, String password) throws Exception {
        File dir = new File(file_name);
        String length = "";
        if (dir.exists() && dir.isDirectory() && verifyPassword(password, file_name)){
            length = new String(Base64.getDecoder().decode(getMetaDataLine(file_name, 0))
                    , StandardCharsets.UTF_8);
            return Integer.parseInt(length) / getScore(file_name);
        }

        return 0;
    }

    @Override
    public byte[] read(String file_name, int starting_position, int len, String password) throws Exception{

        File root = new File(file_name);
        int file_length = length(file_name, password);
        System.out.println("Total File length : " + file_length);
        System.out.println("Read starting pos : " + starting_position);
        System.out.println("Read length : " + len);
        if (starting_position + len -1 > file_length) {
            throw new Exception("File length : " + file_length);
        }

        byte[] iv = getIV(file_name);

        byte[] ivDec = new byte[iv.length];
        System.arraycopy(iv, 0, ivDec, 0, iv.length-1);

        int start_block = starting_position / FILE_SIZE_BYTES;
        int end_block = (starting_position + len - 1) / FILE_SIZE_BYTES;

        System.out.println("Read File Start Block : " + start_block + " End Block : " + end_block);
        byte[] toReturn = new byte[]{};

        // increment IV to the correct pointer
        incrementIV(iv, start_block * (FILE_SIZE_BYTES / ENC_BLOCK_SIZE));

        // read the chunk files, decrypt it and append the bytes
        for ( int i= start_block + 1; i<= end_block +1; i++){
            String blockFile = file_name + File.separator + i;
            byte[] f = read_from_file(new File(blockFile));
//            System.out.println("Reading file : " + blockFile + ", length " + f.length);

            byte[] decrypted = blockDecrypt(f, ivDec, FILE_SIZE_BYTES, ENC_BLOCK_SIZE);
            toReturn = concatenateByteArrayList(Arrays.asList(toReturn, decrypted));
        }

        int sp = Math.max(starting_position % FILE_SIZE_BYTES -1, 0);

        toReturn = splitBytes(toReturn, sp, sp + len - 1);

        return toReturn;
    }


    public byte[] read_new(String file_name, int starting_position, int len, String password) throws Exception{
        File root = new File(file_name);
        int file_length = length(file_name, password);
        System.out.println("File length : " + file_length);
        if (starting_position + len > file_length) {
            throw new Exception("Overflow, Can not read.");
        }

        int start_block = starting_position / FILE_SIZE_BYTES;
        int end_block = (starting_position + len) / FILE_SIZE_BYTES;

        System.out.println("Start block : " + start_block + ", end_block : " + end_block);

        byte[] iv = getIV(file_name);

        byte[] ivDec = new byte[iv.length];
        System.arraycopy(iv, 0, ivDec, 0, iv.length-1);

        // increment IV to the correct pointer
        int incrementBy = start_block * 62;
        System.out.println("IV Increment by " + incrementBy);
        incrementIV(ivDec, incrementBy);

        // ORIGINAL MY CODE - ALMOST WORKING
        byte[] toReturn = "".getBytes();

        String returnString = "";

        int lengthToDecrypt = 0;
        for (int i = start_block + 1; i <= end_block + 1; i++) {
            byte[] temp = read_from_file(new File(root, Integer.toString(i)));
//            temp = blockDecrypt(temp, ivDec, FILE_SIZE_BYTES, ENC_BLOCK_SIZE);

            temp = splitBytesWithSize(temp, 0, FILE_SIZE_BYTES);
            System.out.println("Reading file " + i + ", Read length : " + temp.length);
//            if (i == end_block + 1) {
////                temp = temp.substring(0, starting_position + len - end_block * FILE_SIZE_BYTES);
//                temp = splitBytes(temp, 0, starting_position + len - end_block * FILE_SIZE_BYTES);
//            }
//            if (i == start_block + 1) {
////                temp = temp.substring(starting_position - start_block * FILE_SIZE_BYTES);
//                temp = splitBytes(temp, starting_position - start_block * FILE_SIZE_BYTES, temp.length-1);
//            }

//            System.out.println(Arrays.toString(ivDec));
            byte[] d = blockDecrypt(temp, ivDec, FILE_SIZE_BYTES, ENC_BLOCK_SIZE);

            toReturn = concatenateByteArrayList(Arrays.asList(toReturn, temp));
            System.out.println(new String(d));
            returnString += new String(d);
            lengthToDecrypt +=FILE_SIZE_BYTES;
        }

        System.out.println("Length to decrypt : " + lengthToDecrypt);
        byte[] dec = blockDecrypt(toReturn, ivDec, lengthToDecrypt, ENC_BLOCK_SIZE);

        int sp = starting_position < FILE_SIZE_BYTES ? starting_position: starting_position - start_block*FILE_SIZE_BYTES;

        int ep = starting_position + len - (start_block ) * FILE_SIZE_BYTES;
        ep = Math.max(0, ep);
//        System.out.println("Return String : " + returnString);

//        return splitBytesWithSize(dec, sp, len);

        return returnString.substring(sp, ep).getBytes();
    }

    /**
     * Algorithm:
     *  - Read Metadata file for existing file length, iv
     *  - Use read() method, starting_position and file_length to get the contents of the file
     *      - Read till end of the file, as all the contents will be affected because of inserting text
     *      - Read method will by default give the decrypted text
     *  - starting_position will be adjusted to that entire block itself
     *  - split the text with the starting_position % BLOCK_SIZE to get the position in that block (prefix & suffix)
     *  - Append the new text between the split text
     *  - Append the remaining text till length reaches multiples of 1024 (BLOCK_SIZE)
     *  - Encrypt the text with the IV from metadata file
     *  - Remove the file parts that were read
     *  - Write the new encrypted text into files (Chunks of 1024 bytes into each file)
     *
     * @param file_name - file to write
     * @param starting_position - start position to write
     * @param content - contents to write from starting_position
     * @param password - password for the file
     * @throws Exception
     */
    @Override
    public void write(String file_name, int starting_position, byte[] content, String password) throws Exception {

        String toWrite = byteArray2String(content);

        int file_length = length(file_name, password);
        System.out.println("File length " + file_length);

        int len = toWrite.length();

        if (starting_position  > file_length) {
            System.out.println("Starting pos > file length");
            throw new Exception();
        }

        int startFileBlock = starting_position / FILE_SIZE_BYTES;

        if ( file_length == 0){
            System.out.println("No contents exists in the file. Creating File chunk 1");
            new File(file_name, "1").createNewFile();
        }

        byte[] iv = getIV(file_name);

        byte[] ivEnc = new byte[iv.length];
        System.arraycopy(iv, 0, ivEnc, 0, iv.length-1);

        byte[] allBlocks;
        int sp = startFileBlock * FILE_SIZE_BYTES;
        sp = Math.max(sp, 0);
        int ep = file_length - sp;

//        incrementIV(iv, start_block * (FILE_SIZE_BYTES / ENC_BLOCK_SIZE));
        incrementIV(ivEnc, sp * (FILE_SIZE_BYTES / ENC_BLOCK_SIZE));

        // if starting and ending position == 0, then it's the first position to start writing
        System.out.println(sp);
        System.out.println(ep);
        allBlocks = sp == ep ? "".getBytes() : read(file_name, sp, ep, password);
        String allBlocksString = new String(allBlocks);


        int splitPoint = starting_position % FILE_SIZE_BYTES-1;
        if (splitPoint < 0 )
            splitPoint = 0;

        System.out.println("Total READ Length : " + allBlocksString.length());
        String prefix = allBlocksString.substring(0, splitPoint);
        String suffix = allBlocksString.substring(splitPoint);

        System.out.println("Prefix Length " + prefix.length());
        System.out.println("Suffix Length " + suffix.length());
        System.out.println("To Write length " + toWrite.length());

        StringBuilder finalMessage = new StringBuilder(prefix + toWrite + suffix);
        System.out.println("Write Output length : " + finalMessage.length());

        int paddedLength = (int)Math.ceil((double)finalMessage.length()/FILE_SIZE_BYTES) * FILE_SIZE_BYTES;
        System.out.println("Expected Padded Length : " + paddedLength);


        int updated_file_len = finalMessage.length();

        while ( finalMessage.length() < paddedLength){
            finalMessage.append("\0");
        }

        int sf = startFileBlock + 1;
        int ef = finalMessage.length() / FILE_SIZE_BYTES;

        System.out.println("Start File for writing : " + sf);
        System.out.println("End File to write : " + ef);
        System.out.println("Actual Padded Length : " + finalMessage.length());
        System.out.println("Write Final Output : " + finalMessage);



        updateFileLength(file_name, updated_file_len);
        removeChunkFiles(file_name, sf, ef);

        int i=0;

        while(sf <= ef){
            byte[] chunkFile = finalMessage.substring(i, i + FILE_SIZE_BYTES).getBytes();
            System.out.println("Chunk file " + sf + " length : " + chunkFile.length);
            byte[] enc = blockEncrypt(chunkFile, ivEnc, ENC_BLOCK_SIZE);
            byte[] hmac = calculateHMAC(enc, iv);
            byte[] signedEncBytes = concatenateByteArrayList(Arrays.asList(enc, hmac));
            save_to_file(signedEncBytes, new File(file_name, String.valueOf(sf++)));
            i +=FILE_SIZE_BYTES;
        }

        System.out.println("Successfully Written.");
    }

    public void over_write(String file_name, int starting_position, byte[] content, String password) throws Exception {

        int file_length = length(file_name, password);
        System.out.println("File length " + file_length);

        int len = content.length;

        if (starting_position  > file_length) {
            System.out.println("Starting pos > file length");
            throw new Exception();
        }

        starting_position = Math.max(starting_position - 1, 0);

        int ending_position = starting_position + len;
        int startFileBlock = starting_position / FILE_SIZE_BYTES;

        // FILE_SIZE_BYTES added to ensure that the block is covered till 992 bytes
        int endFileBlock = (ending_position + FILE_SIZE_BYTES) / FILE_SIZE_BYTES;

        if ( file_length == 0){
            System.out.println("No contents exists in the file. Creating File chunk 1");
            new File(file_name, "1").createNewFile();
        }

        // start and end block to be read
        // the complete block files will be read
        int readFrom = startFileBlock * FILE_SIZE_BYTES;
        int readTill = Math.min(endFileBlock * FILE_SIZE_BYTES, file_length);
        int readLength = readTill - readFrom;

        byte[] iv = getIV(file_name);

        byte[] ivEnc = new byte[iv.length];
        System.arraycopy(iv, 0, ivEnc, 0, iv.length-1);

        int incrementIvBy = readFrom / ENC_BLOCK_SIZE;
        System.out.println("Incrementing iv by : " + incrementIvBy);
        incrementIV(ivEnc, incrementIvBy);

        // For a given starting_position X, sp has to end till X-1 position.
        int sp = Math.max(starting_position % FILE_SIZE_BYTES-1, 0);
//        int ep = ending_position >= file_length ? file_length-1: ending_position % FILE_SIZE_BYTES;
        int ep = sp + len+1;
        // to ensure that the file is trimmed correctly
        if ( ep >= file_length )
            sp = file_length-1;


        System.out.println("SP : " + sp + ", EP : " + ep);
        System.out.println("Read from : " + readFrom + ", Read Till : " + readTill + ", Read Length: " + readLength);

        byte[] decContents = read_new(file_name, readFrom, readLength, password);
        System.out.println("READ String : " + new String(decContents));
        System.out.println("Dec Length " + decContents.length);
        int suffixEndPoint = Math.min(decContents.length-1, readTill);

        // if starting position == 0, then there will be no prefix.
        byte[] prefix = starting_position == 0 ? "".getBytes() : splitBytes(decContents, 0, sp);
        // If toWrite length > file length, then there will be no suffix at all.
        byte[] suffix ;

        // if the toWrite length is exceeding the existing contents
        // then the file length will be extended, and thus no suffix
        if ( len + starting_position > file_length ) {
            System.out.println("Ignoring suffix chars");
            suffix = "".getBytes();
        }else{
            suffix = splitBytes(decContents, ep, suffixEndPoint);
        }

        // need to check this
//        incrementIV(ivEnc, sp * (FILE_SIZE_BYTES / ENC_BLOCK_SIZE));

        System.out.println("Prefix length " + prefix.length);
        System.out.println("Prefix : " + new String(prefix));
        System.out.println("Suffix length " + suffix.length);
        System.out.println("Suffix : " + new String(suffix));
        System.out.println("To write length " + content.length);
        byte[] updatedBytes = concatenateByteArrayList(Arrays.asList(prefix, content, suffix));

        int updatedLength = updatedBytes.length;

        System.out.println("Updated length before padding " + updatedLength);
        int toPadLength = updatedLength % FILE_SIZE_BYTES == 0 ? updatedLength : (updatedLength/FILE_SIZE_BYTES + 1) * FILE_SIZE_BYTES;

//        System.out.println("Updated String before padding : " + new String(updatedBytes));

        updatedBytes = nullPadding(updatedBytes, toPadLength-1);
        System.out.println("Updated string after padding : " + new String(updatedBytes));
        System.out.println("Final padded String length : " + updatedBytes.length);

        updatedLength = updatedBytes.length;

        int i=0;
        for ( int j= startFileBlock +1; j<= endFileBlock + 1; j++){
            byte[] chunkFile = splitBytes(updatedBytes, i, i + FILE_SIZE_BYTES-1);
//            System.out.println("Chunk file " + j + " length : " + chunkFile.length);
            byte[] enc = blockEncrypt(chunkFile, ivEnc, ENC_BLOCK_SIZE);
            byte[] hmac = calculateHMAC(enc, iv);
            byte[] signedEncBytes = concatenateByteArrayList(Arrays.asList(enc, hmac));
            System.out.println("HMAC Final size : " + signedEncBytes.length);
            save_to_file(signedEncBytes, new File(file_name, String.valueOf(j)));
            i +=FILE_SIZE_BYTES-1;
        }

        if (updatedLength > file_length) {
            System.out.println("Overflow, updating file length");
            updateFileLength(file_name, updatedLength);
        }

        System.out.println("File written successfully");
    }

    /**
     * Steps to consider...:<p>
  	 *  - verify password
     *  - Find the length and the total file blocks available
     *  - Each file, last 32 bytes are the HMAC, read each file and get the contents of them
     *  - Calculate the HMAC of the remaining 992 bytes (1024 - 32) and check if they are equal to the pre-existing HMAC
     *  - If any of the file doesn't match, return false
     *  - Check also includes metadata file as well.
     */
    @Override
    public boolean check_integrity(String file_name, String password) throws Exception {

        int status = 0;

        if ( verifyPassword(password, file_name ) ){

            int length = length(file_name, password);
            byte[] iv = getIV(file_name);
            int totalFiles = (int)Math.ceil( (double) length / 992);
            System.out.println("Total files : " + totalFiles);

            for ( int i=0; i<=totalFiles ; i++){
                String f = file_name + File.separator + i;
                byte[] msg = read_from_file(new File(f));
                byte[] enc = splitBytes(msg, 0, 991);
                String fileHMAC = new String(splitBytes(msg, 992, 1024));
                String calculatedHMAC = new String(calculateHMAC(enc, iv));
                if (calculatedHMAC.equals(fileHMAC)) {
                    System.out.println("File " + f + ", status : invalid");
                    status++;
                }
            }

        }else{
            throw new PasswordIncorrectException();
        }

    	return status == 0;
  }

    /**
     * Steps to consider... <p>
     *  - verify password <p>
     *  - truncate the content after the specified newLength <p>
     *  - re-pad, update metadata and HMAC <p>
     */
    @Override
    public void cut(String file_name, int len, String password) throws Exception {

        File root = new File(file_name);
        int file_length = length(file_name, password);

        if (len > file_length) {
            System.out.println("Length too long than contents of the file");
            throw new Exception("Length too long than contents of the file");
        }
        int end_block = (len) / FILE_SIZE_BYTES;

        byte[] iv = getIV(file_name);

        byte[] ivDec = new byte[iv.length];
        System.arraycopy(iv, 0, ivDec, 0, iv.length-1);

        byte[] ivEnc = new byte[iv.length];
        System.arraycopy(iv, 0, ivEnc, 0, iv.length-1);

        File file = new File(root, Integer.toString(end_block + 1));

        // decrypt the file and save as string
//        byte[] dec = decript_AES(read_from_file(file), iv);
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
            file.delete();
            cur++;
        }

        updateFileLength(file_name, len);
    }
  
}
