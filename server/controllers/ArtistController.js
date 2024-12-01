const ArtistModel = require('../models/ArtistModel')
const UploadIMG = require('./Upload')
const fs = require("fs");

module.exports.GetAll = async(req, res)=>{
    var data = await ArtistModel.find()
    return res.status(200).json({
        message: "Lấy thành công danh sách nghệ sĩ",
        data: data
    })
}

module.exports.Create = async(req, res)=>{
    try{
        let {name, desc} = req.body
        var art = await ArtistModel.create({
            name,
            desc
        })
        return res.status(201).json({
            status: "create artist success",
            message: "Tạo nghệ sĩ thành công",
            data: art
        })
    }
    catch(e)
    {
        console.log("Error at ArtistController - create: ", e)
        return res.status(400).json({
            status: "create artist failed",
            message: "Tạo nghệ sĩ thất bại",
            data: null
        })
    }

}

module.exports.UpdateImage = async(req, res)=>{
    try{
        let {id} = req.params
        let {root}  = req.vars
        let {file} = req
        var path = `${root}/public/artist/${id}`

        var nameImg = await UploadIMG.uploadSingle(file, path, id)
        if(nameImg)
        {
            var art = await ArtistModel.findOneAndUpdate({_id: id}, {image: nameImg}, {new: true})
            return res.status(200).json({
                message: "Cập nhật ảnh thành công",
                data: art
            })
        }
        else{
            return res.status(400).json({
                message: "Cập nhật ảnh thất bại",
                data: null
            })
        }
    }
    catch (e)
    {
        console.log("Error at Artist Controller - UpdateImage: ", e)
        return res.status(500).json({
            status: "Update image artist failed",
            message: "Sửa ảnh nghệ sĩ thất bại. Thử lại sau",
            data: null
        })
    }
}

module.exports.Update = async(req, res)=>{
    const { id } = req.params; // Lấy ID của nghệ sĩ từ params
    const updateFields = req.body; // Lấy dữ liệu từ body
    try {
        // Chỉ update các trường được gửi trong body
        const updatedArtist = await ArtistModel.findByIdAndUpdate(
            id,
            { $set: updateFields },
            { new: true, runValidators: true } // `new: true` để trả về document sau khi update
        );

        if (!updatedArtist) {
            return res.status(404).json({
                message: "Không tìm thấy nghệ sĩ",
            });
        }

        res.status(200).json({
            message: "Cập nhật nghệ sĩ thành công",
            data: updatedArtist,
        });

    } catch (error) {
        console.error("Error updating artist: ", error);
        res.status(500).json({
            message: "Có lỗi xảy ra khi cập nhật nghệ sĩ",
            error: error.message,
        });
    }

}
