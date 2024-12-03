const EventModel = require('../../models/EventModel')
const AccountModel = require('../../models/AccountModel')
const ArtistModel = require('../../models/ArtistModel')
const mongoose = require("mongoose");

const validateEvent = (req, res, next) => {
    const { name, location, desc, type, date, priceRange } = req.body;

    // Kiểm tra các trường bắt buộc
    if (!name || !location || !desc || !Array.isArray(type) || type.length < 1) {
        return res.status(400).json({
            message: "Vui lòng cung cấp đầy đủ các thông tin thiết yếu: name, location, desc, và ít nhất 1 type."
        });
    }

    if (!date || !date.start || !date.end) {
        return res.status(400).json({
            message: "Vui lòng cung cấp đầy đủ thông tin ngày bắt đầu (start) và ngày kết thúc (end)."
        });
    }

    if (!priceRange || priceRange.min == null || priceRange.max == null) {
        return res.status(400).json({
            message: "Vui lòng cung cấp thông tin giá cả (priceRange) bao gồm min và max."
        });
    }

    if (priceRange.min < 0 || priceRange.max < 0 || priceRange.min > priceRange.max) {
        return res.status(400).json({
            message: "Giá cả (priceRange) không hợp lệ. Giá tối thiểu phải nhỏ hơn hoặc bằng giá tối đa và không được âm."
        });
    }

    // Kiểm tra ngày hợp lệ
    try {
        req.body.date.start = parseDateTime(date.start);
        req.body.date.end = parseDateTime(date.end);

        if (req.body.date.start >= req.body.date.end) {
            return res.status(400).json({
                message: "Ngày bắt đầu phải nhỏ hơn ngày kết thúc."
            });
        }
    } catch (error) {
        return res.status(400).json({
            message: "Định dạng ngày giờ không hợp lệ. Vui lòng sử dụng định dạng DD/MM/YYYY HH:mm."
        });
    }

    return next();
};

const isExistId = async (req, res, next) =>{
    var {id} = req.params
    if(!id)
    {
        return res.status(400).json({
            message: "Thiếu id sự kiện"
        })
    }
    // Kiểm tra nếu id không phải là ObjectId hợp lệ
    if (!mongoose.Types.ObjectId.isValid(id)) {
        return res.status(400).json({
            message: "Id không hợp lệ!"
        });
    }
    var event = await EventModel.findOne({_id: id})
        .populate({
            path: 'news', // Tên tham chiếu tới bảng `news`
            options: { sort: { createdAt: -1 } } // Sắp xếp theo `createdAt` giảm dần
        });

    if(!event)
    {
        return res.status(400).json({
            message: "Sự kiện không tồn tại"
        })
    }
    req.vars.Event = event
    return next()
}

const updateEvent = async(req, res, next)=>{
    try {
        const { id } = req.params; // Lấy id của sự kiện từ URL
        const updateData = {}; // Dữ liệu đã qua kiểm tra sẽ lưu ở đây
        const body = req.body; // Dữ liệu từ request

        // Kiểm tra và xử lý các trường cần cập nhật
        if (body.name) updateData.name = body.name;
        if (body.location) updateData.location = body.location;
        if (body.desc) updateData.desc = body.desc;
        if (Array.isArray(body.type)) updateData.type = body.type;
        if (Array.isArray(body.tag)) updateData.tag = body.tag;

        if (body.priceRange) {
            var {min, max} = body.priceRange
            if (min != null && max != null && min < max && min >= 0)
            {
                updateData["priceRange.min"] = body.priceRange.min;
                updateData["priceRange.max"] = body.priceRange.max;
            }

        }

        // if(body.isTicketPosition && (typeof body.isTicketPosition === "boolean"))
        // {
        //     updateData.isTicketPosition = body.isTicketPosition
        // }

        try {
            var {start, end} = body.date
            if(start && end)
            {
                start = parseDateTime(start)
                end = parseDateTime(end)
                if(start < end)
                {
                    updateData["date.start"] = start;
                    updateData["date.end"] = end;
                }
            }
        }
        catch (error) {

        }


        if (body.isHotEvent != null && (typeof body.isHotEvent === 'boolean')) updateData.isHotEvent = body.isHotEvent;



        // if (Array.isArray(body.followers))
        // {
        //
        //     // Lọc ra các follower hợp lệ
        //     const validFollowers = [];
        //     for (const followerId of body.followers) {
        //         if (mongoose.Types.ObjectId.isValid(followerId)) {
        //             const accountExists = await AccountModel.exists({ _id: followerId });
        //             if (accountExists) {
        //                 validFollowers.push(followerId);
        //             }
        //         }
        //     }
        //
        //     if (validFollowers.length > 0) {
        //         updateData.followers = validFollowers;
        //     }
        // }

        if (Array.isArray(body.artists))
        {
            // updateData.artists = body.artists;
            const validArtist = []
            for (const artist of body.artists) {
                if (mongoose.Types.ObjectId.isValid(artist)) {
                    const accountExists = await ArtistModel.exists({ _id: artist });
                    if (accountExists) {
                        validArtist.push(artist);
                    }
                }
            }

            if (validArtist.length > 0) {
                updateData.artists = validArtist;
            }
        }


        req.vars.updateData = updateData

    } catch (error) {
        // console.error('Error updating event:', error.message);
        // res.status(500).json({ message: 'Internal server error' });
    }
    return next()
}

const parseDateTime = (input) => {
    const [datePart, timePart] = input.split(" ");
    const [day, month, year] = datePart.split("/").map(Number);
    const [hour, minute] = timePart.split(":").map(Number);
    return new Date(year, month - 1, day, hour, minute);
};

const UpdateTicketPrice = async(req, res, next)=>{

    var {location, type} =req.query
    var {price} = req.body
    if(!location && !type)
    {
        return res.status(400).json({
            message: "Vui lòng cung cấp query 'location' 'type'"
        })
    }
    if(!price || typeof price !== 'number' || price <= 0)
    {
        return res.status(400).json({
            message: "Vui lòng cung cấp body 'price' và price phải là số > 0 "
        })
    }
    return next()
}
module.exports.Create = validateEvent;
module.exports.IsExistEvent = isExistId;
module.exports.Update = updateEvent;
module.exports.UpdateTicketPrice = UpdateTicketPrice;

