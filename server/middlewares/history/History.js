const HistoryModel = require('../../models/HistoryEventModel')
const AccountModel = require('../../models/AccountModel')
const EventModel = require('../../models/EventModel')
const mongoose = require("mongoose");

module.exports.CanPost = async (req, res, next)=>{
    try{
        var {User} = req.vars
        var userId = User._id

        var { event, rating, comment } = req.body;
        rating = Number(rating); // Chuyển rating thành số

        var mess = null

        if(!event || !rating || !comment)
        {
            mess = "Vui lòng cung cấp đủ 'event', 'rating', 'comment'. rating phải là số nguyên từ 1 đến 5"
        }
        else if(!mongoose.isValidObjectId(event))
        {
            mess = "Id sự kiện không hợp lệ"
        }
        else if(isNaN(rating) || !Number.isInteger(rating) || rating < 1 || rating > 5)
        {

            mess = "rating phải là số nguyên từ 1 đến 5";
        }
        else{
            var his = await HistoryModel.findOne({
                event: event,
                account: userId
            })
            if(his)
            {
                mess = "Bạn đã đánh giá rồi. Không thể tạo mới!"
            }
            else{
                var Event = await EventModel.findById(event)
                if(!Event)
                {
                    mess = "Sự kiện không tồn tại"
                }
                else{
                    var isJoin = Event.accJoins?.includes(userId)
                    if(!isJoin)
                    {
                        mess = "Bạn chưa tham gia sự kiện này"
                    }
                }
            }
        }

        if(mess)
        {
            return res.status(400).json({
                message: mess
            })
        }
        else{
            req.body.rating = rating
            return  next()
        }


    }
    catch (e) {
        console.log("Error at History Middleware - CanPost", e)
        return res.status(500).json({
            message: "Vui lòng thử lại sau"
        })
    }
}
module.exports.CanDelete = async (req, res, next)=>{
    try{
        var {id} = req.params
        var {User} = req.vars

        if(!mongoose.isValidObjectId(id))
        {
            return res.status(400).json({
                message: "Id không hợp lệ"
            })
        }
        var his = await HistoryModel.findById(id)
        if(!his)
        {
            return res.status(400).json({
                message: "Không có lịch sử tham gia nào để xóa"
            })
        }
        if(his.account?.toString() !== User._id.toString())
        {
            return res.status(400).json({
                message: "Bạn không có quyền xóa tài nguyên này"
            })
        }
        return next()
    }
    catch (e) {
        console.log("Error at History Middleware - CanDelete", e)
        return res.status(500).json({
            message: "Vui lòng thử lại sau"
        })
    }

}