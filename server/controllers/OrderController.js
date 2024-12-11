const TicketInfoModel = require('../models/TicketInfoModel')
const TicketModel = require('../models/TicketModel')
const AccountModel = require('../models/AccountModel')
const BuyTicketModel = require('../models/BuyTicket')
const EventModel = require("../models/EventModel");
const jwt = require("jsonwebtoken");
const SECRET_ORDER = process.env.SECRET_ORDER || "secret-key-order-px4"
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
                    ticket: ticket
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
            status: "waiting"
        });

        if (existBuyTicket) {
            // Lọc những ticketInfo chưa có
            const newTickets = listInfo.filter((id) => !existBuyTicket.ticketInfo.includes(id));

            // Thêm các ticket info mới vào danh sách
            existBuyTicket.ticketInfo.push(...newTickets);

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
        console.error("OrderController - Create: ", error);
        return res.status(500).json({
            message: "Đã xảy ra lỗi khi tạo đơn.",
            error: error.message,
        });
    }
}

module.exports.Update = async(req, res)=> {
    try {
        const {BuyTicket, User, Update} = req.vars;



        const updatedEvent = await BuyTicketModel.findOneAndUpdate(
            {
                _id: BuyTicket._id
            },
            {$set: Update}, // Cập nhật chỉ các trường hợp lệ
            {new: true, runValidators: true} // Trả về dữ liệu sau khi cập nhật và kiểm tra tính hợp lệ
        )
            .populate({
                path: 'members', // Liên kết members
                select: 'name email image address point' // Chỉ trả về các trường cần thiết
            })
            .populate('event') // Liên kết event
            .populate({
                path: 'accCreate', // Liên kết accCreate
                select: 'name email image address point' // Chỉ trả về các trường cần thiết
            })
            .populate({
                path: 'ticketInfo', // Liên kết ticketInfo
                match: {
                    $or: [
                        { accPay: { $exists: false } }, // ticketInfo không có accPay
                        { 'ticket.accBuy': { $exists: false } } // ticket trong ticketInfo không có accBuy
                    ]
                },
                populate: {
                    path: 'ticket', // Liên kết sâu tới ticket trong ticketInfo
                    populate: {
                        path: 'info', // Liên kết tiếp tới info trong ticket
                        model: 'ticket_types'
                    }
                }
            })

        return res.status(200).json({
            message: "Chỉnh sửa thành công",
            data: updatedEvent,
        });

    } catch (error) {
        console.error("OrderController - Update: ", error);
        return res.status(500).json({
            message: "Đã xảy ra lỗi khi tạo đơn.",
            error: error.message,
        });
    }

}

module.exports.GetCheckOut = async(req, res)=>{
    try{
        var {User, Coupon, BuyTicket, Infos} = req.vars
        var {infos} = req.body
        var CouponDiscount = 0
        var totalPrice = 0
        var DiscountMoney = 0
        var FinalMoney = 0

        if(BuyTicket.typePayment === "all")
        {
            // Tính tổng `price` từ `ticket_types`
            totalPrice = BuyTicket.ticketInfo.reduce((total, ticketInfo) => {
                const ticketPrice = ticketInfo.ticket?.info?.price || 0; // Lấy giá tiền, nếu không có thì là 0
                return total + ticketPrice;
            }, 0);
            DiscountMoney = BuyTicket.discount?.percentDiscount ? (BuyTicket.discount?.percentDiscount*totalPrice)/100 : 0
        }
        else if(BuyTicket.typePayment === "single")
        {
             totalPrice = Infos.reduce((acc, info) => {
                acc += info.ticket.info.price;
                return acc;
            }, 0);
        }
        else{
            return res.status(400)
                .json({
                    message: "Loại thanh toán không hợp lệ"
                })
        }

        if(Coupon)
        {
            CouponDiscount = Coupon.maxDiscount < 0
                ? (Coupon.percentDiscount * totalPrice) / 100
                : Math.min((Coupon.percentDiscount * totalPrice) / 100, Coupon.maxDiscount);
        }

        FinalMoney = Math.max(totalPrice - DiscountMoney - CouponDiscount, 100)

        var data = {
            User: User,
            Type:  BuyTicket.typePayment.toLowerCase(),
            BuyTicket: BuyTicket,
            Coupon: Coupon??"",
            Price: totalPrice,
            Discount: DiscountMoney,
            CouponDiscount: CouponDiscount,
            FinalPrice: FinalMoney,
            Infos: Infos
        }
        var dataSign ={
            User: User._id,
            Type: "all",
            BuyTicket: BuyTicket._id,
            Coupon: Coupon?._id??"",
            Price: totalPrice,
            Discount: DiscountMoney,
            CouponDiscount: CouponDiscount,
            FinalPrice: FinalMoney,
            Infos: infos
        }

        await jwt.sign(dataSign, SECRET_ORDER, {expiresIn: "5m"}, (err, tokenLogin)=>{
            if(err) throw err
            return res.status(200).json({
                status: "Lấy thông tin thanh toán thành công",
                message: "Lấy thông tin thanh toán thành công",
                data: data,
                token: tokenLogin
            })
        })
    }
    catch (error) {
        console.error("OrderController - GetCheckOut: ", error);
        return res.status(500).json({
            message: "Lỗi khi lấy thông tin",
            error: error.message,
        });
    }
}