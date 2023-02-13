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

    public boolean verifyPassword(String password, String file_name) throws Exception {
        System.out.println("Validating password");

        byte[] original_hash_bytes = splitBytes(Base64.getDecoder().decode(getMetaDataLine(file_name, 2))
        , 0, 47);
        byte[] line4_bytes= Base64.getDecoder().decode(getMetaDataLine(file_name, 3).substring(0, 127));
        byte[] salt_bytes = splitBytes(line4_bytes, 0, 15);
        byte[] new_password_hash = getPasswordHash(salt_bytes
                , password.getBytes(StandardCharsets.UTF_8));

        return Arrays.equals(original_hash_bytes, new_password_hash);
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

    /**
     * Steps to consider...:<p>
     *  - verify password <p>
     *  - check if requested starting position and length are valid <p>
     *  - decrypt content data of requested length 
     */
    @Override
    public byte[] read(String file_name, int starting_position, int len, String password) throws Exception {
    	return null;
    }

    
    /**
     * Steps to consider...:<p>
	 *	- verify password <p>
     *  - check if requested starting position and length are valid <p>
     *  - ### main procedure for update the encrypted content ### <p>
     *  - compute new HMAC and update metadata 
     */
    @Override
    public void write(String file_name, int starting_position, byte[] content, String password) throws Exception {
        String str_content = byteArray2String(content);
        File root = new File(file_name);
        int file_length = length(file_name, password);

        if (starting_position > file_length) {
            throw new Exception();
        }


        int len = str_content.length();
        int start_block = starting_position / Config.BLOCK_SIZE;
        int end_block = (starting_position + len) / Config.BLOCK_SIZE;
        for (int i = start_block + 1; i <= end_block + 1; i++) {
            int sp = (i - 1) * Config.BLOCK_SIZE - starting_position;
            int ep = (i) * Config.BLOCK_SIZE - starting_position;
            String prefix = "";
            String postfix = "";
            if (i == start_block + 1 && starting_position != start_block * Config.BLOCK_SIZE) {

                prefix = byteArray2String(read_from_file(new File(root, Integer.toString(i))));
                prefix = prefix.substring(0, starting_position - start_block * Config.BLOCK_SIZE);
                sp = Math.max(sp, 0);
            }

            if (i == end_block + 1) {
                File end = new File(root, Integer.toString(i));
                if (end.exists()) {

                    postfix = byteArray2String(read_from_file(new File(root, Integer.toString(i))));

                    if (postfix.length() > starting_position + len - end_block * Config.BLOCK_SIZE) {
                        postfix = postfix.substring(starting_position + len - end_block * Config.BLOCK_SIZE);
                    } else {
                        postfix = "";
                    }
                }
                ep = Math.min(ep, len);
            }

            String toWrite = prefix + str_content.substring(sp, ep) + postfix;

            while (toWrite.length() < Config.BLOCK_SIZE) {
                toWrite += '\0';
            }

            save_to_file(toWrite.getBytes(), new File(root, Integer.toString(i)));
        }


        // Update metadata
        // - Read the first line of the metadata file (decrypt for length)
        // - add the length of contents to it
        // - encrypt it back
        // - update the HMAC of the contents

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
