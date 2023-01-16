"use strict";
/*import crypto from "crypto";
const algorithm = 'aes-256-cbc';

// Defining key
const key = crypto.randomBytes(32);

// Defining iv
const iv =  Buffer.alloc(16, 0)

export function encrypt(value: string) {

    // Creating Cipheriv with its parameter
    let cipher = crypto.createCipheriv('aes-256-cbc', Buffer.from(key), iv);

    // Updating text
    let encrypted = cipher.update(value);

    // Using concatenation
    encrypted = Buffer.concat([encrypted, cipher.final()]);

    // Returning iv and encrypted data
    return {
        iv: iv,
        encryptedData: encrypted.toString('hex')
    };
}

// A decrypt function
export function decrypt(value: string, ivr:string) {
    value : {
    }

    let iv = Buffer.from(ivr, 'hex');
    let encryptedText =
        Buffer.from(value, 'hex');

    // Creating Decipher
    let decipher = crypto.createDecipheriv(
        'aes-256-cbc', Buffer.from(key), iv);

    // Updating encrypted text
    let decrypted = decipher.update(encryptedText);
    decrypted = Buffer.concat([decrypted, decipher.final()]);

    // returns data after decryption
    return decrypted.toString();
}*/ 
