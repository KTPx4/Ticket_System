const TicketInfoModel = require('../models/TicketInfoModel')
const TicketModel = require('../models/TicketModel')
const AccountModel = require('../models/AccountModel')
const BuyTicketModel = require('../models/BuyTicket')
const EventModel = require("../models/EventModel");

module.exports.GetAll= async(req, res)=>{
    try{
        var {User} = req.vars
        var listBuyTicket = await BuyTicketModel.find({
            $or: [
                { accCreate: User._id },
                { members: User._id }
            ]
        })
            .populate('members')
            .populate('accCreate')
            .populate('event')
            .populate('ticketInfo')
            .populate('discount')
            .populate('coupon')
            .populate('payment')

        return res.status(200).json({
            message: "Lấy danh sách thành công",
            data: listBuyTicket
        })
    }
    catch (e)
    {
        console.log("OrderController - GetAll:", e)
        return res.status(500).json({
            message: "Lỗi server, thử lại sau"
        })
    }
}
module.exports.GetByEvent = async(req, res)=>{
    try{
        var {Event, User} = req.vars
        var listBuyTicket = await BuyTicketModel.find({
            event: Event._id,
            $or: [
                { accCreate: User._id },
                { members: User._id }
            ]
        })
            .populate('members')
            .populate('accCreate')
            .populate('event')
            .populate('ticketInfo')
            .populate('discount')
            .populate('coupon')
            .populate('payment')

        return res.status(200).json({
            message: "Lấy thành công",
            data: listBuyTicket
        })
    }
    catch (e)
    {
        console.log("OrderController - GetAll:", e)
        return res.status(500).json({
            message: "Lỗi server, thử lại sau"
        })
    }
}

module.exports.Create = async (req, res) => {
    try {
        const { Event, User, ListTicket } = req.vars;

        const userId = User._id;
        const eventId = Event._id;

        // Tìm và tạo mới các ticket info nếu chưa tồn tại
        const listInfo = await Promise.all(
            ListTicket.map(async (ticket) => {
                let TicketInfo = await TicketInfoModel.findOne({
                    event: eventId,
                    ticket: ticket,
                });

                if (!TicketInfo) {
                    TicketInfo = await TicketInfoModel.create({
                        event: eventId,
                        ticket: ticket,
                    });
                }
                return TicketInfo._id; // Chỉ trả về ID để giảm kích thước dữ liệu
            })
        );

        // Kiểm tra nếu đã có BuyTicket
        let existBuyTicket = await BuyTicketModel.findOne({
            accCreate: userId,
            event: eventId,
        });

        if (existBuyTicket) {
            // Thêm các ticket info mới vào danh sách
            existBuyTicket.ticketInfo.push(...listInfo);
            await existBuyTicket.save();

            return res.status(200).json({
                message: "Đã thêm vé",
                data: existBuyTicket,
            });
        } else {
            // Tạo mới BuyTicket
            const BuyTicket = await BuyTicketModel.create({
                accCreate: userId,
                ticketInfo: listInfo,
                event: eventId,
            });

            return res.status(201).json({
                message: "Đã tạo đơn hàng thành công",
                data: BuyTicket,
            });
        }
    } catch (error) {
        console.error("OrderController - Create: ",error);
        return res.status(500).json({
            message: "Đã xảy ra lỗi khi tạo đơn.",
            error: error.message,
        });
};

module.exports.Update = async(req, res)=>{
    try{
        const { Event, User, Update } = req.vars;

        const userId = User._id;
        const eventId = Event._id;

        const updatedEvent = await BuyTicketModel.findByIdAndUpdate(
            {
                accCreate: userId,
                event: eventId
            },
            { $set: Update }, // Cập nhật chỉ các trường hợp lệ
            { new: true, runValidators: true } // Trả về dữ liệu sau khi cập nhật và kiểm tra tính hợp lệ
        );
        return res.status(200).json({
            message: "Chỉnh sửa thành công",
            data: updatedEvent,
        });

    }catch (error) {
        console.error("OrderController - Update: ",error);
        return res.status(500).json({
            message: "Đã xảy ra lỗi khi tạo đơn.",
            error: error.message,
        });
    }
    }
}