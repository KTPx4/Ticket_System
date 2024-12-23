const mongoose = require("mongoose");
const EventModel = require('../../models/EventModel')
module.exports.Create = async(req, res, next)=>{
    let {event}= req.query

    if(!event)
    {
        return res.status(400).json({
            message: "Vui lòng cung cấp id sự kiện '?event='"
        })
    }
    // Kiểm tra nếu id không phải là ObjectId hợp lệ
    if (!mongoose.Types.ObjectId.isValid(event)) {
        return res.status(400).json({
            message: "Id event không hợp lệ!"
        });
    }


    var eventModel = await EventModel.findOne({_id: event})
    if(!eventModel)
    {
        return res.status(400).json({
            message: "Sự kiện không tồn tại"
        });
    }

    req.vars.Event = eventModel
    return next()
}