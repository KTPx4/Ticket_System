

const acceptAVT = ["jpg", "png"]
const acceptTrailer = ["jpg", "png", "mp4"]
const path = require('path')
const multer = require("multer");
const fs = require('fs').promises

const allowedMimeTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp', 'video/mp4', 'video/mpeg'];

module.exports.Single = async(req, res, next)=>
{
    let file = req.file
    let {root} = req.vars

    if(!file)
    {
        return res.status(400).json({
            message: "Vui lòng cung cấp ảnh: 'image'"
        })
    }
    const name = file.originalname;
    const temp = name.split('.');
    const extension = temp[temp.length - 1];
    if(!acceptAVT.includes(extension))
    {

        // Lấy đường dẫn tuyệt đối của file
        const filePath = path.join(root, '/uploads', file.filename);

        // Xóa file nếu định dạng không hợp lệ
        fs.unlink(filePath, (err) => {
            if (err) {
                console.log("Lỗi khi xóa file:", err.message);
            }
        });

        return res.status(400).json({
            message: "Ảnh không đúng định dạng!"
        })
    }
    return next()
}
module.exports.Trailer = (req, res, next)=>{
    let file = req.file
    let {root} = req.vars
    if(!file)
    {
        return res.status(400).json({
            message: "Vui lòng cung cấp trailer: 'trailer'"
        })
    }
    const name = file.originalname;
    const temp = name.split('.');
    const extension = temp[temp.length - 1];
    
    if(!acceptTrailer.includes(extension))
    {
        // Lấy đường dẫn tuyệt đối của file
        const filePath = path.join(root, 'uploads', file.filename);

        // Xóa file nếu định dạng không hợp lệ
        fs.unlink(filePath, (err) => {
            if (err) {
                console.log("Lỗi khi xóa file:", err.message);
            }
        });

        return res.status(400).json({
            message: "Định dạng không được hỗ trợ!"
        })
    }
    return next()
}

module.exports.UploadArr = (req, res, next)=>{
    var {root} = req.vars
    // Cấu hình multer
    const storage = multer.diskStorage({
        destination: (req, file, cb) => {
            cb(null, path.join(root, '/uploads/'));
        },
        filename: (req, file, cb) => {
            const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9);
            cb(null, `${file.fieldname}-${uniqueSuffix}${path.extname(file.originalname)}`);
        },
    });

    const uploader = multer({ storage ,
        limits: { files: 5 }, // Giới hạn số lượng file
    });

    const upload = uploader.array('files', 5); // Giới hạn file ở đây

    upload(req, res, (err) => {
        if (err instanceof multer.MulterError) {
            if (err.code === 'LIMIT_UNEXPECTED_FILE') {
                // Xử lý lỗi quá số lượng file
                return res.status(400).json({
                    message: 'Số lượng file vượt quá giới hạn cho phép (5 file).'
                });
            }
        } else if (err) {
            // Các lỗi khác
            return res.status(500).json({ message: 'Lỗi tải lên file.' });
        }
        // Kiểm tra số lượng file thủ công
        if (req.files && req.files.length > 5) {
            return res.status(400).json({
                message: 'Số lượng file vượt quá giới hạn cho phép (5 file).'
            });
        }
        // Nếu không có lỗi, chuyển tiếp middleware
        return next();
    });
}
module.exports.ArrayFile = async(req, res, next)=>{
    try{
        if (!req.files || req.files.length === 0) {
            return next();
        }
        else {
            // Kiểm tra từng file trong danh sách
            for (const file of req.files) {
                if (!allowedMimeTypes.includes(file.mimetype)) {
                    return res.status(400).json({
                        message: `File không hợp lệ: ${file.originalname}. Chỉ chấp nhận ảnh (jpeg, png, gif, webp) hoặc video (mp4, mpeg).`,
                    });
                }
            }

            // Nếu tất cả file hợp lệ, chuyển sang bước tiếp theo
            next();
        }
    }
    catch (e) {
        console.log("Upload validator : ",e)
        return  res.status(500).json({
            message: "Vui lòng thử lại sau"
        })
    }

}