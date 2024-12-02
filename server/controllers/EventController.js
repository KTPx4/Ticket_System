const EventModel = require('../models/EventModel')
const Upload = require("./Upload");
module.exports.GetAll = async(req, res)=>{
    try{
        var data = await EventModel.find()
            .populate('followers')
            .populate('artists')
            .populate({
                path: 'news', // Tên tham chiếu tới bảng `news`
                options: { sort: { createdAt: -1 } } // Sắp xếp theo `createdAt` giảm dần
            })
            .populate('accJoins')
        return res.status(200).json({
            message: "Lấy dữ liệu sự kiện thành công",
            data: data
        })
    }
    catch (e)
    {
        console.log("Error at EventController - GetAll: ", e)
        return res.status(500).json({
            message: "Lấy thông tin thất bại, thử lại sau"
        })
    }

}
module.exports.GetByID= async (req, res)=>{
    try{
       var {Event} = req.vars
        return res.status(200).json({
            message: "Lấy dữ liệu sự kiện thành công",
            data: Event
        })
    }
    catch (e)
    {
        console.log("Error at EventController - GetAll: ", e)
        return res.status(500).json({
            message: "Lấy thông tin thất bại, thử lại sau"
        })
    }
}
module.exports.Create = async(req, res)=>{
    try {
        const { name, location, desc, type, date, priceRange, isTicketPosition } = req.body;
        var isPosition = false
        if(isTicketPosition && isTicketPosition === true)
        {
            isPosition = true
        }
        const newEvent = await EventModel.create({
            name,
            location,
            desc,
            type,
            date,
            priceRange,
            isTicketPosition: isPosition
        });
        if(isPosition)
        {

        }
        return res.status(201).json({ message: "Sự kiện đã được tạo thành công", data: newEvent });

    }
    catch (error) {
        console.log("Error at EventController - Create: " , error)
        return res.status(500).json({ message: "Có lỗi xảy ra khi tạo sự kiện", error: error.message });
    }
}

module.exports.Update = async(req, res)=>{
    var {updateData}= req.vars // get update from validator
    var {id} = req.params

    // Cập nhật tài liệu
    // Nếu không có trường hợp lệ nào để cập nhật
    if (Object.keys(updateData).length === 0) {
        return res.status(400).json({ message: 'Không có giá trị để sửa' });
    }

    const updatedEvent = await EventModel.findByIdAndUpdate(
        id,
        { $set: updateData }, // Cập nhật chỉ các trường hợp lệ
        { new: true, runValidators: true } // Trả về dữ liệu sau khi cập nhật và kiểm tra tính hợp lệ
    );

    res.status(200).json({
        message: "Chỉnh sửa sự kiện thành công",
        data: updatedEvent
    });
}

module.exports.PutTrailer = async(req, res)=>{
    try{
        let {id} = req.params
        let {root}  = req.vars
        let {file} = req
        var path = `${root}/public/event/${id}/trailer`

        var nameImg = await Upload.uploadSingle(file, path, id)
        if(nameImg)
        {
            var art = await EventModel.findOneAndUpdate({_id: id}, {trailer: nameImg}, {new: true})
            return res.status(200).json({
                message: "Cập nhật trailer thành công",
                data: art
            })
        }
        else{
            return res.status(400).json({
                message: "Cập nhật trailer thất bại",
                data: null
            })
        }
    }
    catch (e)
    {
        console.log("Error at Event Controller - UpdateImage: ", e)
        return res.status(500).json({
            status: "Update trailer event failed",
            message: "Sửa trailer thất bại. Thử lại sau",
            data: null
        })
    }
}

module.exports.DeleteByID= async(req, res)=>{
    var {Event} = req.vars
    try{
        var id = Event._id
        // Xóa sự kiện
        await EventModel.findByIdAndDelete(id);

        return res.status(200).json({
            message: "Xóa sự kiện thành công.",
            data: Event
        });

    }
    catch (e) {
        console.log("Error at EventController - DeleteByID: ", e)
        return res.status(500).json({
            message: "Xóa sự kiện thất bại, thử lại sau"
        })
    }
}
module.exports.getNews = async(req, res)=>{
    try{
        var {Event} = req.vars
        return res.status(200).json({
            message: "Lấy dữ liệu thành công",
            data: Event.news
        })
    }
    catch (e) {
        console.log("Error at EventController - GetAll: ", e)
        return res.status(500).json({
            message: "Lấy thông tin thất bại, thử lại sau"
        })
    }
}