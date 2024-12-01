

const acceptAVT = ["jpg", "png"]
const acceptTrailer = ["jpg", "png", "mp4"]
const path = require('path')
const fs = require('fs').promises

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