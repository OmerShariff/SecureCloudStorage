# SecureCloudStorage
In this project an alternate encryption technique was demonstrated in which rather than the traditional method of encrypting data after uploading to cloud, encryption is performed before uploading so that it remains unreadable and unaccessible to the third party cloud storage service providers. AES, DES and RC4 encrytion algorithms are used.

There are primarily two applications in this project

Cloud: Cloud is a local storage which acts as an virtual cloud environment.
Cloud User: This application allows user to signup with cloud and then login. 

After the user uploads a file, it gets divided into blocks of data and each block will be encrypted by performing AES, DES and RC4 algorithm.
This encryted data is then stored into the virtual cloud storage.
The user can download and decrypt those blocks and integrate all bloacks to restore the original file. 
