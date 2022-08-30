"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.schemaUser = void 0;
exports.schemaUser = {
    username: {
        type: String,
        required: true,
        length: {
            min: 3,
            max: 36
        },
        test: /^[a-z0-9]+$/gi
    }
};
//https://www.npmjs.com/package/schema-validator
