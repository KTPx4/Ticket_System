const ArtistModel = require('../models/ArtistModel')
const AccountModel = require('../models/AccountModel')
const mongoose = require('mongoose')

const UploadIMG = require('./Upload')
const fs = require("fs");

module.exports.GetAll = async(req, res)=>{
    var data = await ArtistModel.find()
    return res.status(200).json({
        message: "Lấy thành công danh sách nghệ sĩ",
        data: data
    })
}

module.exports.GetByID = async(req, res)=>{
    var {id} = req.params
    if (!mongoose.Types.ObjectId.isValid(id))
    {
        return res.status(400).json({
            message: "Id không hợp lệ"
        })
    }
    var data = await ArtistModel.findById(id)
    return res.status(200).json({
        message: "Lấy thành công nghệ sĩ",
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
module.exports.Follow = async (req, res) => {
    try {
        const { User, Artist } = req.vars; // User và Artist từ req.vars
        const accountId = User._id;
        const artistId = Artist._id;

        // Sử dụng session để đảm bảo tính nhất quán khi cập nhật cả hai nơi
        const session = await mongoose.startSession();
        session.startTransaction();

        try {
            // Kiểm tra và thêm artistId vào follows.artist của tài khoản
            const accountUpdate = await AccountModel.findByIdAndUpdate(
                accountId,
                {
                    $addToSet: { "follows.artist": artistId } // Chỉ thêm nếu chưa có
                },
                { new: true, session } // Cập nhật và sử dụng session
            );

            // Kiểm tra và thêm accountId vào follower của nghệ sĩ
            const artistUpdate = await ArtistModel.findByIdAndUpdate(
                artistId,
                {
                    $addToSet: { followers: accountId } // Chỉ thêm nếu chưa có
                },
                { new: true, session } // Cập nhật và sử dụng session
            );

            // Commit transaction
            await session.commitTransaction();
            session.endSession();

            // Phản hồi thành công
            return res.status(200).json({
                status: "success",
                message: "Theo dõi nghệ sĩ thành công",
                data: {
                    account: accountUpdate,
                    artist: artistUpdate
                }
            });
        } catch (e) {
            // Rollback nếu có lỗi
            await session.abortTransaction();
            session.endSession();
            throw e;
        }
    } catch (e) {
        console.error("Error at Artist Controller - Follow: ", e);
        return res.status(500).json({
            status: "error",
            message: "Server đang lỗi, thử lại sau."
        });
    }
};

module.exports.UnFollow = async(req, res)=>{
    try {
        const { User, Artist } = req.vars; // User và Artist từ req.vars
        const accountId = User._id;
        const artistId = Artist._id;

        // Sử dụng session để đảm bảo tính nhất quán khi cập nhật cả hai nơi
        const session = await mongoose.startSession();
        session.startTransaction();

        try {
            // Xóa artistId khỏi follows.artist của tài khoản
            const accountUpdate = await AccountModel.findByIdAndUpdate(
                accountId,
                {
                    $pull: { "follows.artist": artistId } // Xóa phần tử khỏi mảng
                },
                { new: true, session } // Cập nhật và sử dụng session
            );

            // Xóa accountId khỏi follower của nghệ sĩ
            const artistUpdate = await ArtistModel.findByIdAndUpdate(
                artistId,
                {
                    $pull: { followers: accountId } // Xóa phần tử khỏi mảng
                },
                { new: true, session } // Cập nhật và sử dụng session
            );

            // Commit transaction
            await session.commitTransaction();
            session.endSession();

            // Phản hồi thành công
            return res.status(200).json({
                status: "success",
                message: "Bỏ theo dõi nghệ sĩ thành công",
                data: {
                    account: accountUpdate,
                    artist: artistUpdate
                }
            });
        } catch (e) {
            // Rollback nếu có lỗi
            await session.abortTransaction();
            session.endSession();
            throw e;
        }


    } catch (e) {
        console.error("Error at Artist Controller - UnFollow: ", e);
        return res.status(500).json({
            status: "error",
            message: "Server đang lỗi, thử lại sau."
        });
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
