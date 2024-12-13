const EventModel = require('../models/EventModel')
const TicketTypeModel = require('../models/TicketTypeModel')
const TicketModel = require('../models/TicketModel')
const NewsModel = require('../models/NewsModel')
const mongoose = require('mongoose')
const moment = require('moment');

const Upload = require("./Upload");
const fs = require("fs");
const jwt = require("jsonwebtoken");
const AccountModel = require("../models/AccountModel");
const JWT_SCAN_SECRECT = process.env.KEY_SECRET_SCAN_TICKET ?? ""

module.exports.GetAll = async(req, res)=>{
    try{
        // Lấy type từ query
        const { type , name, date, location } = req.query;
        // Khởi tạo điều kiện tìm kiếm
        const query = {};
        if (type) {
            query.type = { $regex: new RegExp(type, 'i') }; // Không phân biệt hoa thường
        }
        if (location) {
            query.location = { $regex: new RegExp(location, 'i') }; // Không phân biệt hoa thường
        }

        // Thêm điều kiện tìm kiếm theo name nếu có
        if (name) {
            query.name = { $regex: new RegExp(name, 'i') }; // Không phân biệt hoa thường
        }

        // Tìm kiếm theo date nếu có
        if (date) {
            const parsedDate = moment(date, 'DD/MM/YYYY'); // Chuyển đổi date sang Moment
            if (parsedDate.isValid()) {
                // Lấy ngày bắt đầu và kết thúc
                const startOfDay = parsedDate.startOf('day').toDate();
                const endOfDay = parsedDate.endOf('day').toDate();

                // Thêm điều kiện tìm kiếm ngày
                query['date.start'] = {
                    $gte: startOfDay, // Lớn hơn hoặc bằng ngày bắt đầu
                    $lte: endOfDay   // Nhỏ hơn hoặc bằng ngày kết thúc
                };
            } else {
                return res.status(400).json({
                    message: 'Ngày không hợp lệ. Định dạng đúng là DD/MM/YYYY.'
                });
            }
        }


        var data = await EventModel.find(query)
            .populate('followers')
            .populate('artists')
            .populate({
                path: 'news', // Tên tham chiếu tới bảng `news`
                options: { sort: { createdAt: -1 } } // Sắp xếp theo `createdAt` giảm dần
            })
            .populate('accJoins')
        return res.status(200).json({
            message: "Lấy dữ liệu sự kiện thành công",
            length: data.length ?? 0,
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
        var {root} = req.vars
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

        // create ticket for event
        await createTicket(newEvent)
        await createFolder(root, newEvent._id, newEvent.image)

        return res.status(201).json({ message: "Sự kiện đã được tạo thành công", data: newEvent });

    }
    catch (error) {
        console.log("Error at EventController - Create: " , error)
        return res.status(500).json({ message: "Có lỗi xảy ra khi tạo sự kiện", error: error.message });
    }
}
module.exports.createTicket = async (newEvent) => {
    try {
        const locations = ["A", "B", "C"]; // Danh sách các khu vực
        const types = ["Vip 1", "Vip 2", "Normal"]; // Danh sách các loại vé

        // Tạo mảng chứa tất cả các loại vé cần tạo
        const ticketTypes = locations.flatMap(location =>
            types.map(type => ({
                event: newEvent._id,
                typeTicket: type,
                location: location,
            }))
        );

        // Thêm toàn bộ ticket types vào database một lần
        const insertedTicketTypes = await TicketTypeModel.insertMany(ticketTypes);

        // Tạo vé với số thứ tự tiếp nối
        for (const location of locations) {
            let position = 1; // Khởi tạo số thứ tự từ 1 cho mỗi khu vực

            for (const type of types) {
                const ticketType = insertedTicketTypes.find(
                    tt => tt.location === location && tt.typeTicket === type
                );

                if (!ticketType) continue;

                const tickets = [];

                for (let i = 0; i < 20; i++) {
                    tickets.push({
                        event: newEvent._id,
                        position: position++, // Số thứ tự tự động tăng
                        desc: `Vé vị trí ${position - 1} cho loại ${ticketType.typeTicket} tại khu vực ${ticketType.location}`,
                        info: ticketType._id,
                        isAvailable: true,
                    });
                }

                // Thêm tất cả ticket cho ticket type hiện tại
                await TicketModel.insertMany(tickets);
                // console.log(`Đã tạo vé cho loại ${ticketType.typeTicket} tại khu vực ${ticketType.location}`);
            }
        }

        console.log("Đã tạo các loại vé thành công!");
    } catch (error) {
        console.error("Lỗi khi tạo loại vé:", error);
    }
};

module.exports.getTicket = async(req, res)=>{
    try {
        var eventId = req.vars.Event._id
        var {type, location} = req.query

        // Xây dựng bộ lọc động
        const matchFilter = { event: new mongoose.Types.ObjectId(eventId) };
        if (type) {
            matchFilter['ticketType.typeTicket'] = type; // Lọc theo type
        }
        if (location) {
            matchFilter['ticketType.location'] = location; // Lọc theo location
        }

/*
        var typeByLocation = await TicketTypeModel.aggregate([
            {
                $match: { event: eventId} // Lọc theo eventId
            },
            {
                $group: {
                    _id: "$location", // Gom nhóm theo location
                    types: {
                        $push: {
                            typeTicket: "$typeTicket",
                            price: "$price"
                        }
                    }
                }
            },
            {
                $project: {
                    _id: 0, // Loại bỏ _id mặc định của MongoDB
                    location: "$_id", // Chuyển _id thành location
                    types: 1 // Giữ nguyên danh sách types
                }
            }
        ]);
        */
        const result = await TicketModel.aggregate([
            {
                $match: { event: new mongoose.Types.ObjectId(eventId) }
            },
            {
                $lookup: {
                    from: 'ticket_types',
                    localField: 'info',
                    foreignField: '_id',
                    as: 'ticketType'
                }
            },
            {
                $unwind: '$ticketType'
            },
            // Áp dụng bộ lọc động sau khi join
            {
                $match: matchFilter
            },
            {
                $group: {
                    _id: {
                        location: '$ticketType.location',
                        typeTicket: '$ticketType.typeTicket'
                    },
                    tickets: {
                        $push: {
                            _id: '$_id',
                            name: '$name',
                            position: '$position',
                            desc: '$desc',
                            isAvailable: '$isAvailable',
                            accBuy: '$accBuy',
                            expiresAt: '$expiresAt'
                        }
                    },
                    price: { $first: '$ticketType.price' } // Lấy giá trị price từ ticketType
                }
            },
            {
                $group: {
                    _id: '$_id.location',
                    types: {
                        $push: {
                            type: '$_id.typeTicket',
                            tickets: '$tickets',
                            price: '$price' // Bao gồm giá trị price trong kết quả
                        }
                    }
                }
            },
            {
                $project: {
                    _id: 0,
                    location: '$_id',
                    types: 1
                }
            }
        ]);


        return res.status(200).json({
            data: result
        });
    } catch (error) {
        console.error("Lỗi khi lấy danh sách ticket:", error);
        throw error;
    }
}

module.exports.UpdateTicketPrice = async (req, res)=>{
    var {location, type} =req.query
    var {price} = req.body
    var {id} = req.params
    var typeTicket = await  TicketTypeModel.findOneAndUpdate({event: id, typeTicket: type, location: location }, {price: price} ,{new: true})
    return res.status(200).json({
        message: "Yêu cầu sửa giá được duyệt",
        data: typeTicket
    })
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

module.exports.PutImage = async(req, res)=>{
    try{
        let {id} = req.params
        let {root}  = req.vars
        let {file} = req
        var path = `${root}/public/event/${id}`

        var nameImg = await Upload.uploadSingle(file, path, id)
        if(nameImg)
        {
            var art = await EventModel.findOneAndUpdate({_id: id}, {image: nameImg}, {new: true})
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
        // Delete news, ticket type, ticket
        await TicketModel.deleteMany({event: id})
        await TicketTypeModel.deleteMany({event: id})
        await NewsModel.deleteMany({event: id})

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
module.exports.ScanTicket = async(req, res)=>{
    try{
        var {token} = req.body
        var idEvent = req.params.id
        var {Event}= req.vars

        if(!JWT_SCAN_SECRECT)
        {
            return res.status(400).json({
                message: "Thiếu biến môi trường JWT_SCAN_SECRECT"
            })
        }
        await jwt.verify(token, JWT_SCAN_SECRECT, async(err, data)=>{
            if(err)
            {
                return res.status(400).json({
                    status: 'Invalid Token',
                    message: 'Token lỗi hoặc hết hạn'
                })
            }
            // console.log(data)
            var {idUser, idTicket} = data

            if(!idUser || !idTicket)
            {
                return res.status(400).json({
                    status: 'Invalid Token',
                    message: 'Token không đúng định dạng'
                })
            }

            var Ticket = await TicketModel.findOne({
                _id: idTicket,
                accBuy: idUser,
                isValid: true
            })
            var Account = await AccountModel.findOne({
                _id: idUser
            })

            if(!Ticket || !Account || Ticket.event.toString() !== Event._id.toString())
            {
                // console.log(Ticket, Account,  Event._id ,Ticket.event !== Event._id)
                return res.status(400).json({
                    status: 'Invalid Ticket',
                    message: 'Vé không hợp lệ'
                })
            }

            // Kiểm tra nếu idUser đã tồn tại trong accJoins
            // if (Event.accJoins.includes(idUser)) {
            //     return res.status(400).json({
            //         status: 'User Already Joined',
            //         message: 'Người dùng đã tham gia sự kiện'
            //     });
            // }

            Ticket.isValid = false
            await Ticket.save()

            // Thêm idUser vào accJoins nếu chưa tồn tại
            await EventModel.updateOne(
                { _id: idEvent },
                { $addToSet: { accJoins: idUser } }
            );

            return res.status(200).json({
                message: "Quét thành công"
            })
        })
    }
    catch (e) {
        console.log("EventController-Scan: ", e)
        return res.status(500).json({
            message: "Thử lại sau!"
        })
    }

}
const createFolder = (root, id, nameAvt)=>
{

    const ROOT_AVT = root + "/public/event"

    let defaultAvt = `${ROOT_AVT}/default.png`

    let folderAccount = ROOT_AVT + "/" + id

    let UserAvt = `${ROOT_AVT}/${id}/${nameAvt}`

    try
    {
        if (!fs.existsSync(folderAccount))
        {
            fs.mkdirSync(folderAccount);
        }

        if(!fs.existsSync(UserAvt) && fs.existsSync(defaultAvt))
        {
            // console.log("ok, ", UserAvt);
            fs.copyFileSync(defaultAvt, UserAvt)
        }
    }
    catch(err)
    {
        console.log("Error at EventController - Create Folder Img  ", err);
        return false;
    }
    return true;
    // Kiểm tra xem tệp tin nguồn tồn tại hay không
}