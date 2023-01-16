var AWS = require('aws-sdk');

const s3 = new AWS.S3({apiVersion: '2006-03-01'});


const con = ()=>{
    s3.listBuckets(function(err:any, data:any) {
        if (err) {
          console.log("Error", err);
        } else {
          console.log("Success", data.Buckets);
        }
      });
}

const upladImages = (file:any) =>{

    const params = {
      //Bucket: process.env.BUCKET_REPORT_IMAGES,
      //Key: `${filename} .jpgnpm install --save multer-s3`,
      //Body: fileContent
    }
    
    return new Promise ( (resolve, reject) => {

      s3.upload(params, (err:any, data:any) => {
        if (err) {
          reject(err)
        }
        resolve(data.Location)
      })
    }
    )
  }


(module).exports = {
  con,
}