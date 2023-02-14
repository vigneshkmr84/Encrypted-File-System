
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

    public static byte[] splitBytes(byte[] array, int sp, int ep) {
        return Arrays.copyOfRange(array, sp, ep+ 1);
    }

    public static byte[] splitBytesWithSize(byte[] array, int sp, int len) {
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

    public byte[] generateNewMetaFile(String user_name, String password) throws Exception {

        /*StringBuilder sb = new StringBuilder();

        // 16 bytes = 128 bits
        byte[] saltBytes = secureRandomNumber(16);
        byte[] ivBytes = secureRandomNumber(16);

        byte[] passBytes = password.getBytes(StandardCharsets.UTF_8);
        byte[] paddedPass = concatenateByteArrayList(Arrays.asList(saltBytes, passBytes));
        byte[] hashedPass = hash_SHA384(paddedPass);

        String line1 = String.valueOf(0);
        String line2 = user_name;
        String line3 = Base64.getEncoder().encodeToString(concatenateByteArrayList(Arrays.asList(hashedPass, saltBytes, ivBytes)));
        String line4 = Base64.getEncoder().encodeToString("hmac".getBytes());

        sb.append(line1).append(System.lineSeparator())
                .append(line2).append(System.lineSeparator())
                .append(line3).append(System.lineSeparator())
                .append(line4).append(System.lineSeparator());

        return sb.toString().getBytes();*/

        String randChars = "abcdefghijklmnopqrstuvwxyz";
        randChars += randChars.toUpperCase() + "0123456789";
        StringBuilder sb = new StringBuilder();

        // 16 bytes = 128 bits
        byte[] saltBytes = secureRandomNumber(16);
        byte[] ivBytes = secureRandomNumber(16);

        byte[] passBytes = password.getBytes(StandardCharsets.UTF_8);
        /*byte[] paddedPass = concatenateByteArrayList(Arrays.asList(saltBytes, passBytes));
        byte[] hashedPass = hash_SHA384(paddedPass);*/
        byte[] hashedPass = getPasswordHash(saltBytes, passBytes);

        String line1 = Base64.getEncoder().encodeToString("0".getBytes());
        String line2 = Base64.getEncoder().encodeToString(user_name.getBytes());
        String line3 = Base64.getEncoder().encodeToString(randomPadding(hashedPass, 128));
        String line4 = Base64.getEncoder().encodeToString(randomPadding(concatenateByteArrayList(Arrays.asList(saltBytes, ivBytes)), 256));
        String line5 = Base64.getEncoder().encodeToString(randomPadding("01234567899876543210123456789012".getBytes(), 128));

        sb.append(line1).append(System.lineSeparator())
                .append(line2).append(System.lineSeparator())
                .append(line3).append(System.lineSeparator())
                .append(line4).append(System.lineSeparator())
                .append(line5).append(System.lineSeparator());

        System.out.println("line 3,4,5 length : " + (line3.length() + line4.length() + line5.length()));
        int length = sb.toString().length();
        System.out.println("initial length : " + length);

        // padding for remaining bytes
        while ( sb.toString().length() + 1 <= Config.BLOCK_SIZE){
            sb.append(randChars.charAt(Math.abs(secureRandomNumber(1)[0]) % randChars.length()));
        }
        System.out.println("Total size after padding : " + sb.length());

        return sb.toString().getBytes();
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
        save_to_file(generateNewMetaFile(user_name, password), meta_file);
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
        if (starting_position + len > file_length) {
            throw new Exception();
        }

        byte[] iv = "2b7e151628aed2a6abf71589".getBytes();
//        byte[] iv = getIV(file_name);

        System.out.println("IV : " + new String(iv));
        int start_block = starting_position / Config.BLOCK_SIZE;
        int end_block = (starting_position + len - 1) / Config.BLOCK_SIZE;

        System.out.println("Read File Start Block : " + start_block + " End Block : " + end_block);
        byte[] toReturn = new byte[]{};

        for ( int i= start_block + 1; i<= end_block +1; i++){
            String blockFile = file_name + File.separator + i;
            byte[] f = read_from_file(new File(blockFile));
            System.out.println("Reading file : " + blockFile + ", length " + f.length);
            toReturn = concatenateByteArrayList(Arrays.asList(toReturn, decript_AES(f, iv)));
        }
        System.out.println(toReturn.length);
        int sp = Math.max(starting_position % Config.BLOCK_SIZE -1, 0);

        toReturn = splitBytes(toReturn, sp, sp + len - 1);

        return toReturn;

    }
    /**
     * Steps to consider...:<p>
     *  - verify password <p>
     *  - check if requested starting position and length are valid <p>
     *  - decrypt content data of requested length 
     */
    /*@Override
    public byte[] read(String file_name, int starting_position, int len, String password) throws Exception {

        byte[] message = new byte[]{};

//        if ( verifyPassword(password, file_name) ){
            System.out.println("Valid password");
//            int file_length = length(file_name, password);
            int file_length = 1216;

            int start_block = starting_position / Config.BLOCK_SIZE;
            int end_block = (starting_position + len) / Config.BLOCK_SIZE;



            byte[] iv = "Ecstaticadvanced".getBytes();

            // starting chunk in start_block
            int start_chunk = (starting_position % Config.BLOCK_SIZE) / AES_BLOCK_SIZE;
            // ending chunk in end_block
            int end_chunk = ((starting_position + file_length )% Config.BLOCK_SIZE) / AES_BLOCK_SIZE;

            System.out.println("Start Block : " + start_block);
            System.out.println("End Block : " + end_block);

            System.out.println("Start Chunk : " + start_chunk );
            System.out.println("End Chunk : " + end_chunk);
            System.out.println(start_block == end_block);
            for ( int i=start_block + 1; i<=end_block+1; i++){

                File f = new File(file_name, Integer.toString(i));
                byte[] contents = read_from_file(f);

                byte[] trim = new byte[]{};
                // if the pointer reaches the end block, then read only till the end chunk
                // else read through the entire remaining file
                if ( i == end_block+1){
                    int sp =  start_block  == end_block ? start_block : 0;

                    for ( int j = sp; j<=end_chunk-1; j++) {
                        byte[] d = decript_AES(splitBytes(contents, j * AES_BLOCK_SIZE, (j + 1) * AES_BLOCK_SIZE - 1), iv );
//                        System.out.println(new String(d));
                        // decrypt and concatenate
                        trim = concatenateByteArrayList(Arrays.asList(trim, d));
                    }

                }else{
                    for ( int j=start_chunk; i< Config.BLOCK_SIZE/AES_BLOCK_SIZE; j++) {
                        byte[] d = decript_AES(splitBytes(contents, j * AES_BLOCK_SIZE, (j + 1) * AES_BLOCK_SIZE - 1), iv);
//                        System.out.println(new String(d));
                        trim = concatenateByteArrayList(Arrays.asList(trim, d));
                    }
                }
                System.out.println(new String(trim));
                message = concatenateByteArrayList(Arrays.asList(trim, message));
            }
//        }else{
//            throw new PasswordIncorrectException();
//        }
//        message = splitBytes(message, starting_position*(start_chunk + 1) % AES_BLOCK_SIZE +1, (starting_position + len -1 ) * (end_chunk + 1)% AES_BLOCK_SIZE );
        System.out.println(message.length);
        // WORKING
        message = splitBytesWithSize(message, starting_position % AES_BLOCK_SIZE, len);
//        message = new String(message).substring(starting_position % AES_BLOCK_SIZE, len).getBytes();
    	return message;
    }*/

    public byte[] blockEncrypt(byte[] message, byte[] iv) throws Exception {

        // contains 128 byte split arrays
        List<byte[]> byteArrayList = new ArrayList<>();

        for ( int i=0; i< message.length; i+=AES_BLOCK_SIZE )
            byteArrayList.add(splitBytes(message, i, i+AES_BLOCK_SIZE-1));

        byte[] encryptedMessage = new byte[]{};
        for ( byte[] block : byteArrayList) {
            byte[] encryptedBlock = encript_AES(block, iv);
            concatenateByteArrayList(Arrays.asList(encryptedMessage, encryptedBlock));
        }

        return encryptedMessage;
    }
    
    /**
     * Steps to consider...:<p>
	 *	- verify password <p>
     *  - check if requested starting position and length are valid <p>
     *  - ### main procedure for update the encrypted content ### <p>
     *  - compute new HMAC and update metadata 
     */
    /*@Override
    public void write(String file_name, int starting_position, byte[] content, String password) throws Exception {
        String str_content = byteArray2String(content);
        File root = new File(file_name);
        int file_length = length(file_name, password);

        if (starting_position > file_length) {
            throw new Exception();
        }

        byte[] ivBytes = splitBytes(getMetaDataLine(file_name, 3).getBytes(), 16, 31);

        int messageLen = str_content.length();
        int start_block = starting_position / Config.BLOCK_SIZE;
        int end_block = (starting_position + messageLen) / Config.BLOCK_SIZE;
        for (int i = start_block + 1; i <= end_block + 1; i++) {
            int sp = (i - 1) * Config.BLOCK_SIZE - starting_position;
            int ep = (i) * Config.BLOCK_SIZE - starting_position;
            String prefix = "";
            String postfix = "";

            // need to understand this logic
            if (i == start_block + 1 && starting_position != start_block * Config.BLOCK_SIZE) {

                prefix = byteArray2String(read_from_file(new File(root, Integer.toString(i))));
                prefix = prefix.substring(0, starting_position - start_block * Config.BLOCK_SIZE);
                sp = Math.max(sp, 0);
            }

            if (i == end_block + 1) {
                File end = new File(root, Integer.toString(i));
                if (end.exists()) {

                    postfix = byteArray2String(read_from_file(new File(root, Integer.toString(i))));

                    if (postfix.length() > starting_position + messageLen - end_block * Config.BLOCK_SIZE) {
                        postfix = postfix.substring(starting_position + messageLen - end_block * Config.BLOCK_SIZE);
                    } else {
                        postfix = "";
                    }
                }
                ep = Math.min(ep, messageLen);
            }

            System.out.println("SP : " + sp);
            System.out.println("EP : " + ep);
            String toWrite = prefix + str_content.substring(sp, ep) + postfix;

            while (toWrite.length() < Config.BLOCK_SIZE) {
                toWrite += '\0';
            }

            save_to_file(toWrite.getBytes(), new File(root, Integer.toString(i)));
        }


//          Update metadata
//            - Read the first line of the metadata file (decrypt for length).
//            - add the length of contents to it.
//            - encrypt it back.
//            - update the HMAC of the contents.

        if (content.length + starting_position > length(file_name, password)) {
            String s = byteArray2String(read_from_file(new File(root, "0")));
            String[] strs = s.split("\n");
            String s1 = new String(Base64.getDecoder().decode(strs[0]));
            System.out.println(s1);
            System.out.println(content.length);
            int newSize = Integer.parseInt(s1) + content.length + starting_position;
            System.out.println("New Size : " + newSize);
            newSize = newSize*getScore(file_name);
            System.out.println("Score :" + getScore(file_name));
            System.out.println("Updated Size :" + newSize);
            strs[0] = Base64.getEncoder().encodeToString(Integer.toString(newSize).getBytes());
            String toWrite = "";
            for (String t : strs)
                toWrite += t + "\n";

            while (toWrite.length() < Config.BLOCK_SIZE) {
                toWrite += '\0';
            }
            save_to_file(toWrite.getBytes(), new File(root, "0"));

        }
    }*/

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

        File rooFolder = new File(file_name);
        int file_length = length(file_name, password);
        System.out.println("File length " + file_length);


        if (starting_position > file_length) {
            System.out.println("Starting pos > file length");
            throw new Exception();
        }

        int len = toWrite.length();
        int startFilePos = starting_position / Config.BLOCK_SIZE;
        int totalBlocks = (int)Math.ceil((double)(file_length + len) / Config.BLOCK_SIZE);
        int endFile = (starting_position + len) / Config.BLOCK_SIZE;

        byte[] allBlocks = new byte[]{};
        /*for (int i = start_block + 1; i <= totalBlocks; i++) {
            String blockFile = file_name + File.separator + i;
            byte[] contents = read(file_name, start_block * Config.BLOCK_SIZE, end_block*Config.BLOCK_SIZE, password);
            byte[] message = decript_AES(contents, getIV(file_name));

            allBlocks = concatenateByteArrayList(Arrays.asList(allBlocks, message));
//            new File(blockFile).delete();
        }*/

        int sp = startFilePos * Config.BLOCK_SIZE;
        sp = Math.max(sp, 0);
        int ep = file_length - sp;

        byte[] contents = read(file_name, sp, ep, password);
//        byte[] contents = read(file_name, starting_position, file_length - starting_position -1, password);

        allBlocks = contents;
        String allBlocksString = new String(allBlocks);

//        int breakPoint = Math.min(Config.BLOCK_SIZE, starting_position % Config.BLOCK_SIZE);
        int breakPoint = starting_position % Config.BLOCK_SIZE-1;
        if (breakPoint < 0 )
            breakPoint = 0;

        System.out.println("Total READ Length : " + allBlocksString.length());
        String prefix = allBlocksString.substring(0, breakPoint);
//        String suffix = allBlocksString.substring(starting_position % Config.BLOCK_SIZE, Config.BLOCK_SIZE -1 );
        String suffix = allBlocksString.substring(breakPoint);

        System.out.println("Prefix Length " + prefix.length());
        System.out.println("Suffix Length " + suffix.length());
        System.out.println("To Write length " + toWrite.length());

        String finalMessage = prefix + toWrite + suffix;
        System.out.println("Write Output length : " + finalMessage.length());

        int paddedLength = (int)Math.ceil((double)finalMessage.length()/Config.BLOCK_SIZE) * Config.BLOCK_SIZE;
        System.out.println("Expected Padded Length : " + paddedLength);

        while ( finalMessage.length() < paddedLength){
            finalMessage += "\0";
        }

        System.out.println("Start Block for writing : " + (startFilePos+1));
        System.out.println("Total blocks to write : " + finalMessage.length() / Config.BLOCK_SIZE);
        System.out.println("Actual Padded Length : " + finalMessage.length());
        System.out.println("Write Final Output : " + finalMessage);
    }

    /**
     * Steps to consider...:<p>
  	 *  - verify password <p>
     *  - check the equality of the computed and stored HMAC values for metadata and physical file blocks<p>
     */
    @Override
    public boolean check_integrity(String file_name, String password) throws Exception {
        if ( verifyPassword(password, file_name + File.separator + "0") ){

        }else{
            throw new PasswordIncorrectException();
        }
    	return false;
  }

    /**
     * Steps to consider... <p>
     *  - verify password <p>
     *  - truncate the content after the specified length <p>
     *  - re-pad, update metadata and HMAC <p>
     */
    @Override
    public void cut(String file_name, int length, String password) throws Exception {
    }
  
}
