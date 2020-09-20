# password-hashing
Common applications of a password-based [key derivation function](https://en.wikipedia.org/wiki/Key_derivation_function "https://en.wikipedia.org/wiki/Key_derivation_function") (PBKDF): *create cryptographic keys*, test a *password verification system* and use a personal *password manager*. 

## What's a PBKDF
A *password-based key derivation function* is essentially a hashing algorithm that generates a cryptographic *key* using an input *password* or *passphrase* as a starting point. To prevent [brute-force attacks](https://en.wikipedia.org/wiki/Brute-force_attack "https://en.wikipedia.org/wiki/Brute-force_attack") or [dictionary attacks](https://en.wikipedia.org/wiki/Dictionary_attack "https://en.wikipedia.org/wiki/Dictionary_attack") on the password:
- a second random string of tens to hundreds of bytes is added, known as [*salt*](https://en.wikipedia.org/wiki/Salt_(cryptography) "https://en.wikipedia.org/wiki/Salt_(cryptography)"). This way the attacker has to run a particular password through the hashing algorithm to verify that it matches the hashed output, but repeat this for every possible value of *salt*.
- The hashing algorithm is often iterated thousands of times. This also makes the key derivation process deliberately slower for the attacker.

## How the app works
This application uses [Java's implementation](https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html#secretkeyfactory-algorithms "https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html#secretkeyfactory-algorithms") of [PBKDF2](https://en.wikipedia.org/wiki/PBKDF2 "https://en.wikipedia.org/wiki/PBKDF2"). You can use 3 separate modes:

1. **Generate one-time cryptographic keys**  
Give your text, which is the *password* in this case, and set your desired *key* length. Whenever you're in this mode, a newly-generated *salt* is used, which means you won't be able to get, even for the same text, the same derived key again.

2. **Password verification system**  
This mode demonstrates how a service authenticates a user. Typically, when a user creates a password for an account, their password is hashed with a random salt. The derived key is stored in a database, along with the salt that was used for the hashing. To authenticate a user, the password presented by the user is hashed with the stored salt and compared with the stored key.

   In this mode you can create passwords and verify they're valid. The first time you create a password:
   - a database `password-verification.db` is created in your directory,
   - a random salt is generated and stored in this database, and
   - your password is hashed with this salt, and the derived key is stored in the database.

   Next time you create a password, it will be hashed with the stored salt, and the derived key will be stored in the database. When you want to verify a password, as described above, it will be hashed with the stored salt. If the derived key exists in the database, the password is considered to be valid.

3. **Password manager**  
You access this mode with your master password. Type a service that you want a password for and the desired length for it. A password will be generated, and you'll be able to retrieve it anytime. In this mode, the password-based key derivation takes place twice:

   1.  *password* = service + master password  
      *key = PBKDF(password, salt)*  
   2. *final key = PBKDF(key, salt)*  

   where the *final key* is the password that is generated for your service. The first time you generate a password:
   
   - a database `password-manager.db` is created in your directory,
   - a random salt is generated and stored in this database, and
   - your service is hashed with your master password and the salt, and the derived key is stored in the database, along with your password's length.

   The final key is derived by once again hashing the stored key with the stored salt. To retrieve the password of a service, it is checked if the derived key is stored in the database. If it exists, it is hashed again to retrieve the final key. This way, your passwords are never stored directly in the database. 

## Run the app
- Clone the repository:  

  `git clone https://github.com/andreaslymp/password-hashing.git`  
  `cd password-hashing.git`

- Set your `MASTER_PASSWORD` in `Main.java` so you can safely use the password manager mode.
- Run with `./gradlew --console plain run` 
- A menu of the available options is displayed at all times, just type the option you want!