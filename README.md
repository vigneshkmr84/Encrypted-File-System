# Encrypted File System

The goal of the project is to encrypt the file and perform read, write, and cut operations efficiently.  
Efficiency in measured terms of number of read / write files and performance in terms of encrypt and decrypt functions. At the same time, the security should be intact for the files, so that any tampering of the files is noticed.

The main code is there is `EFS.java` and only basic functions from `Utility.java` are used.

Functions like CTR Mode of Encryption / Decryption with `AES/ECB/NoPadding`, `HMAC` are implemented manually.