const TicketModel = require('../../models/TicketModel')
const BuyTicketModel = require('../../models/BuyTicket')
const AccountModel = require('../../models/AccountModel')
const EventModel = require('../../models/EventModel')
const DiscountModel = require('../../models/DiscountModel')
const TicketInfoModel = require('../../models/TicketInfoModel')
const CouponModel = require('../../models/CouponModel')

const mongoose = require("mongoose");
const {populate} = require("dotenv");
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
            const ticket = await TicketModel.findOne({
                _id: id,
                event: event,
                accBuy: null,
                isValid: true
            });
            if (!ticket) {
                return res.status(400).json({
                    message: `Vé có id '${id}' không tồn tại hoặc đã được bán`,
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
module.exports.isExistsBuyTicket = async (req, res, next)=>{
    var {id} = req.params
    if(!id)
    {
        return res.status(400).json({
            message: "Thiếu id đơn mua vé"
        })
    }
    // Kiểm tra nếu id không phải là ObjectId hợp lệ
    if (!mongoose.Types.ObjectId.isValid(id)) {
        return res.status(400).json({
            message: "Id không hợp lệ!"
        });
    }
    var event = await BuyTicketModel.findById(id)

    if(!event)
    {
        return res.status(400).json({
            message: "Đơn không tồn tại"
        })
    }
    req.vars.BuyTicket = event
    return next()
}
module.exports.Update = async(req, res, next)=>{
   try{
       var {members, typePayment} = req.body
       var {BuyTicket, User} = req.vars
       const updateData = {}; // Dữ liệu đã qua kiểm tra sẽ lưu ở đây
       // members
       if(Array.isArray(members) )
       {
           members = [...new Set(members)];
           updateData.members = members
           if(updateData.members.includes(User._id.toString()))
           {
               return res.status(400).json({ message: "Không thể tự thêm mình vào danh sách thành viên"});
           }
           if(!members.every(member => mongoose.Types.ObjectId.isValid(member)))
           {
               return res.status(400).json({ message: "Có chứa Id member không hợp lệ"});
           }

           const existingMembers = await AccountModel.find({
               _id: { $in: updateData.members }, // Lọc theo danh sách `members`
           });

           if (existingMembers.length !== updateData.members.length) {

               // Tìm những ID không tồn tại
               const existingIds = existingMembers.map(member => member._id.toString());
               const invalidIds = updateData.members.filter(
                   id => !existingIds.includes(id.toString())
               );
               var mess = `Danh sách ID tài khoản không tồn tại: ${invalidIds}`;
               return res.status(400).json({ message: mess});
           }
       }
       else if(members && !Array.isArray(members) && members.length < 1 )
       {
           return res.status(400).json({ message: "Danh sách members không đúng định dạng"});

       }

       if(typePayment )
       {
           if(TypePayment.includes(typePayment.toLowerCase()))
           {
               updateData.typePayment = typePayment.toLowerCase()
           }
           else{
               return res.status(400).json({ message: "'typePayment' chỉ 1 trong 2 giá trị: 'all', 'single'" });
           }
       }

       if (Object.keys(updateData).length === 0) {
           return res.status(400).json({ message: "Không có giá trị để sửa : 'members', 'typePayment''" });
       }

       if(members)
       {

            memberLength = members.length ?? 0
        
            // Tìm discount có memberRange >= memberLength
            const discount = await DiscountModel.findOne({
                event: BuyTicket.event,
                memberRange: { $gte: memberLength },
            })
                .sort({ memberRange: 1 }); // Sắp xếp memberRange tăng dần để lấy model nhỏ nhất phù hợp
        
            if(discount)
            {
                updateData.discount = discount._id
            }
       }

       req.vars.Update = updateData
       return next()
   }
   catch (e) {
       console.log("Lỗi tại validator Update order: ",e)
       return res.status(500).json({
           message: "Lỗi thử lại sau!"
       })
   }
}

module.exports.GetCheckOut =async (req, res, next)=>{
    try{
        var {id} = req.params
        var {coupon, infos} = req.body

        if(coupon)
        {
            var Coupon = await CouponModel.findOne({
                code: coupon
            })
            if(!Coupon || (Coupon.count < 1 && Coupon.type !== "private") || (!Coupon.isValid && Coupon.type === "private"))
            {
                return res.status(400).json({
                    message: "Mã giảm giá không tồn tại hoặc đã hết!"
                })
            }
            req.vars.Coupon = Coupon
        }

        if(!id)
        {
            return res.status(400).json({
                message: "Thiếu id hóa đơn"
            })
        }
        // Kiểm tra nếu id không phải là ObjectId hợp lệ
        if (!mongoose.Types.ObjectId.isValid(id)) {
            return res.status(400).json({
                message: "Id không hợp lệ!"
            });
        }

        //     .populate({
        //         path: 'ticketInfo', // Lấy thông tin từ `ticketInfo`
        //         populate: {
        //             path: 'ticket', // Lấy thông tin từ `tickets`
        //             populate: {
        //                 path: 'info', // Lấy thông tin từ `ticket_types`
        //                 model: 'ticket_types', // Đảm bảo model chính xác
        //             },
        //         },
        //     })
        //     .populate({
        //         path: 'event',
        //         select: 'name'
        //     })
        // .populate('discount')
        var BuyTicket = await BuyTicketModel.findOne({
            _id: id,
            status: "waiting"
        })
        .populate({
            path: 'members', // Liên kết members
            select: 'name email image address point' // Chỉ trả về các trường cần thiết
        })
        .populate({
            path: 'accCreate', // Liên kết accCreate
            select: 'name email image address point' // Chỉ trả về các trường cần thiết
        })
        .populate('event') // Liên kết event
        .populate('discount') // Liên kết discount
        .populate('coupon') // Liên kết coupon
        .populate('payment') // Liên kết payment
        .populate('accPay') // Liên kết accPay
        .populate({
            path: 'ticketInfo', // Liên kết ticketInfo
            // match: {
            //     $and: [
            //         { accPay: { $exists: false } }, // `accPay` không tồn tại
            //         { 'ticket.accBuy': null }, // `ticket.accBuy` bằng null
            //         { 'ticket.isValid': true } // `ticket.isValid` là true
            //     ]
            // },
            populate: {
                path: 'ticket', // Liên kết sâu tới ticket trong ticketInfo
                populate: {
                    path: 'info', // Liên kết tiếp tới info trong ticket
                    model: 'ticket_types'
                }
            }
        })
        .lean(); // Trả về plain object để dễ thao tác hơn

        if(!BuyTicket)
        {
            return res.status(400).json({
                message: "Hóa đơn không tồn tại hoặc đã được thanh toán!"
            });
        }

        var existingInfos = []

        // check type pay
        if(BuyTicket.typePayment === "single")
        {
            console.log(infos)
            if( !infos || !Array.isArray(infos) || infos.length < 1)
            {
                return res.status(400).json({
                    message: "Vui lòng chọn Info vé để thanh toán!"
                });
            }
            // Kiểm tra từng ID có hợp lệ hay không
            const invalidIds = infos.filter(id => !mongoose.Types.ObjectId.isValid(id));
            if (invalidIds.length > 0) {
                return res.status(400).json({
                    message: "Danh sách Info vé chứa ID không hợp lệ!",
                    invalidIds,
                });
            }

            // Kiểm tra ID tồn tại trong cơ sở dữ liệu và lấy toàn bộ dữ liệu
           existingInfos = await TicketInfoModel.find({
                _id: { $in: infos }, // Lọc theo danh sách infos
                event: BuyTicket.event,
                status: "waiting"
            })
                .populate({
                    path: 'ticket', // Lấy thông tin từ `tickets`
                    populate: {
                        path: 'info', // Lấy thông tin từ `ticket_types`
                        model: 'ticket_types', // Đảm bảo model chính xác
                    },
            })

            // Danh sách các ID đã tồn tại
            const existingIds = existingInfos.map(info => info._id.toString());

            // Lọc ra các ID không tồn tại
            const missingIds = infos.filter(id => !existingIds.includes(id.toString()));

            if (missingIds.length > 0) {
                return res.status(400).json({
                    message: "Một số vé không tồn tại hoặc đã được thanh toán!",
                    missingIds, // Danh sách các ID không tìm thấy
                });
            }          
           
            req.vars.Infos = existingInfos
            // check list ticket info
            //..............
        }
        else if(BuyTicket.typePayment === "all")
        {
            BuyTicket.ticketInfo = BuyTicket.ticketInfo.filter(ticketInfo => {
                const ticket = ticketInfo.ticket; // Lấy thông tin ticket từ ticketInfo
                // Điều kiện: loại bỏ nếu ticket.accBuy !== null hoặc ticket.isValid === false
                return ticket && ticket.accBuy === null && ticket.isValid === true;
            });
            existingInfos = BuyTicket.ticketInfo
        }

         // Giả sử existingInfos đã được lấy từ cơ sở dữ liệu
         if (existingInfos && existingInfos.length > 0) {
                
            for (const info of existingInfos) {
                const { ticket } = info; // Lấy ticket từ từng info
                if (ticket) {
                    // Kiểm tra điều kiện accBuy !== null và isValid === false
                    if (ticket.accBuy !== null || ticket.isValid === false) {
                        return res.status(400).json({
                            message: `Vé có id '${ticket._id}' không hợp lệ hoặc đã được mua`
                        });
                    }
                }
            }
        }
        else{
            return res.status(400).json({
                message: `Không có vé để thanh toán`
            });
        }
         
        req.vars.BuyTicket = BuyTicket

        return next()
    }
    catch (e) {
        console.log("Lỗi tại validator order - GetCheckOut : ",e)
        return res.status(500).json({
            message: "Lỗi thử lại sau!"
        })
    }

}

module.exports.ValidOrder = async(req, res, next)=>{
    var {token, secret, payment} = req.body   
    if(!token || !secret || !payment)
    {
        return res.status(400).json({
            message: "Vui lòng cung cấp token, secret, payment"
        })
    }
    return next()
}

module.exports.Delete = async (req, res, next)=>{
    var {info} = req.body
    if( info )
    {
        if (!mongoose.Types.ObjectId.isValid(info)) {
            return res.status(400).json({
                message: "Id không hợp lệ!"
            });
        }
        var Info = await TicketInfoModel.findById(info)
        if(!Info)
        {
            return res.status(400).json({
                message: "Vé không tồn tại trong đơn vé của bạn!"
            });
        }
        req.vars.Info = Info
    }
    return next()
}