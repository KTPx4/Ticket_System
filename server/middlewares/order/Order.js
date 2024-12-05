const TicketModel = require('../../models/TicketModel')
const BuyTicketModel = require('../../models/BuyTicket')
const AccountModel = require('../../models/AccountModel')
const EventModel = require('../../models/EventModel')
const DiscountModel = require('../../models/DiscountModel')

const mongoose = require("mongoose");
const TypePayment = ["single", "all"]
module.exports.Create = async(req, res, next)=>{
    try{
        var {tickets, event} = req.body
        if(!event)
        {
            return res.status(400).json({
                message: "Vui lòng cung cấp id sự kiện: 'event'"
            })
        }
        // if(!Array.isArray(members) || members.length < 1)
        // {
        //     return res.status(400).json({
        //         message: "Vui lòng cung cấp list id thành viên mua vé: 'members'"
        //     })
        // }
        if(!Array.isArray(tickets) || tickets.length < 1)
        {
            return res.status(400).json({
                message: "Vui lòng cung cấp list id vé: 'tickets'"
            })
        }
        // Kiểm tra nếu id không phải là ObjectId hợp lệ
        if (!mongoose.Types.ObjectId.isValid(event)) {
            return res.status(400).json({
                message: "Id sự kiện không hợp lệ!"
            });
        }
        var Event = await EventModel.findOne({_id: event})
        if(!Event)
        {
            return res.status(400).json({
                message: "Sự kiện không tồn tại!"
            });
        }
        req.vars.Event = Event
        var listAcc = []
        var listTicket = []
        // for (const id of members) {
        //     const acc = await AccountModel.findOne({ _id: id });
        //     if (!acc) {
        //         return res.status(400).json({
        //             message: `Tài khoản có id '${id}' không tồn tại`,
        //         });
        //     }
        //     listAcc.push(acc);
        // }

        for (const id of tickets) {
            const ticket = await TicketModel.findOne({ _id: id, event: event});
            if (!ticket) {
                return res.status(400).json({
                    message: `Vé có id '${id}' không tồn tại trong sự kiện`,
                });
            }
            listTicket.push(ticket);
        }

        // req.vars.ListAcc = listAcc
        req.vars.ListTicket = listTicket
        return next()
    }
    catch (e) {
        console.log("Lỗi tại validator create Order: ",e)
        return res.status(500).json({
            message: "Lỗi server. Thử lại sau!"
        })
    }

}

module.exports.Update = async(req, res, next)=>{
    var {members, typePayment} = req.body
    var {Event} = req.vars
    const updateData = {}; // Dữ liệu đã qua kiểm tra sẽ lưu ở đây
    if(Array.isArray(members) && members.length > 0)
    {
        members = [...new Set(members)];
        updateData.members = members
    }

    if(typePayment && TypePayment.includes(typePayment.toLowerCase()))
    {
        updateData.typePayment = typePayment
    }

    if (Object.keys(updateData).length === 0) {
        return res.status(400).json({ message: 'Không có giá trị để sửa' });
    }
    memberLength = members.length ?? 0
    // Tìm discount có memberRange >= memberLength
    const discount = await DiscountModel.findOne({
        event: Event._id,
        memberRange: { $gte: memberLength },
    })
        .sort({ memberRange: 1 }); // Sắp xếp memberRange tăng dần để lấy model nhỏ nhất phù hợp

    if(discount)
    {
        updateData.discount = discount._id
    }

    req.vars.Update = updateData
    return next()
}