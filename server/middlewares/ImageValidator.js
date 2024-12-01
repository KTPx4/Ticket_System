

const acceptAVT = ["jpg", "png"]

module.exports.Single = async(req, res, next)=>
{
    let file = req.file
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
        return res.status(400).json({
            message: "Ảnh không đúng định dạng!"
        })
    }
    return next()
}