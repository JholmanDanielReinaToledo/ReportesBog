export const schemaUser = {
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
