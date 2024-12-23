const EventModel = require('../../models/EventModel')
const TicketTypeModel = require('../../models/TicketTypeModel')
const mongoose = require("mongoose");

module.exports.UpdateAll = async (req, res, next)=>{
    try{
        var { type, location, price} = req.body

        if(!location && !type)
        {
            return res.status(400).json({
                message: "Vui lòng cung cấp 'location' hoặc 'type'"
            });
        }

        if(!price || typeof price !== 'number' || isNaN(price) || price < 1)
        {
            return res.status(400).json({
                message: "Thiếu giá tiền hoặc không đúng định dạng, 'price'"
            });
        }
        return next()
    }
    catch (e) {
        console.log("Lỗi tại validator Ticket - UpdatePrice : ",e)
        return res.status(500).json({
            message: "Lỗi thử lại sau!"
        })
    }
}

module.exports.UpdatePrice = async(req, res, next)=>{

    try{
        var {event, type, location, price} = req.body

        if (!event || !mongoose.Types.ObjectId.isValid(event)) {
            return res.status(400).json({
                message: "Thiếu id hoặc id sự kiện không hợp lệ!: 'event'"
            });
        }
        var Event = await EventModel.findById(event)
        if(!Event)
        {
            return res.status(400).json({
                message: "Sự kiện không tồn tại!"
            });
        }

        if(!location && !type)
        {
            return res.status(400).json({
                message: "Vui lòng cung cấp 'location' hoặc 'type'"
            });
        }
        if(!price || typeof price !== 'number' || isNaN(price) || price < 1)
        {
            return res.status(400).json({
                message: "Thiếu giá tiền hoặc không đúng định dạng, 'price'"
            });
        }


        return next()
    }
    catch (e) {
        console.log("Lỗi tại validator Ticket - UpdatePrice : ",e)
        return res.status(500).json({
            message: "Lỗi thử lại sau!"
        })
    }
}