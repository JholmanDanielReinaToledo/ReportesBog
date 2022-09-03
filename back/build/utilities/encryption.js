"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.decrypt = exports.encrypt = void 0;
const crypto_1 = __importDefault(require("crypto"));
const algorithm = 'aes-256-cbc';
// Defining key
const key = crypto_1.default.randomBytes(32);
// Defining iv
const iv = Buffer.alloc(16, 0);
function encrypt(value) {
    // Creating Cipheriv with its parameter
    let cipher = crypto_1.default.createCipheriv('aes-256-cbc', Buffer.from(key), iv);
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
exports.encrypt = encrypt;
// A decrypt function
function decrypt(value, ivr) {
    value: {
    }
    let iv = Buffer.from(ivr, 'hex');
    let encryptedText = Buffer.from(value, 'hex');
    // Creating Decipher
    let decipher = crypto_1.default.createDecipheriv('aes-256-cbc', Buffer.from(key), iv);
    // Updating encrypted text
    let decrypted = decipher.update(encryptedText);
    decrypted = Buffer.concat([decrypted, decipher.final()]);
    // returns data after decryption
    return decrypted.toString();
}
exports.decrypt = decrypt;
