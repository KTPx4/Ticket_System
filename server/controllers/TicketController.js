const TicketTypeModel = require('../models/TicketTypeModel')

module.exports.UpdatePrice = async (req, res)=>{
    try{
        var {event, type, location, price} = req.body

        // Kiểm tra điều kiện tìm kiếm
        let query = { event };

        if (type && location) {
            // Nếu cả `type` và `location` đều có giá trị, sử dụng regex với flag 'i' (case-insensitive)
            query = {
                event,
                typeTicket: { $regex: new RegExp(type, 'i') }, // Không phân biệt hoa thường
                location: { $regex: new RegExp(location, 'i') } // Không phân biệt hoa thường
            };
        } else if (type) {
            // Nếu chỉ có `type`, sử dụng regex cho `typeTicket`
            query = {
                event,
                typeTicket: { $regex: new RegExp(type, 'i') } // Không phân biệt hoa thường
            };
        } else if (location) {
            // Nếu chỉ có `location`, sử dụng regex cho `location`
            query = {
                event,
                location: { $regex: new RegExp(location, 'i') } // Không phân biệt hoa thường
            };
        }

        // Cập nhật tài liệu
        const updatedTickets = await TicketTypeModel.updateMany(
            query, // Điều kiện tìm kiếm
            { $set: { price } } // Dữ liệu cập nhật
        );
        return res.status(200).json({
            message: `${updatedTickets.modifiedCount} loại vé được chỉnh sửa giá ${price}`
        })

    }
    catch (e) {
        console.log("Err TicketController - UpdatePrice ", e)
        return res.status(500).json({
            message: `Lỗi update, thử lại sau`
        })
    }
}