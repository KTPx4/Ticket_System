# Ticket System

## Overview
Ticket System is an Android application designed for booking event tickets conveniently. It offers various features to enhance user experience, from event searching to online payment integration.

## Features
- **Search Events**: Users can browse and search for events.
- **View Event Details**: Detailed information about events, including time, location, and performers.
- **Online Ticket Booking**: Secure and fast ticket booking process.
- **Seat Selection**: Choose your preferred seats while booking tickets.
- **Multiple Payment Integrations**: Supports MoMo and Stripe for seamless transactions.
- **Watch Event Trailers & Performance Clips**: Preview events with video clips.
- **Electronic Ticket Verification**: Digital verification of purchased tickets.
- **Group Booking**: Special mode for bulk ticket reservations.
- **Exclusive Offers & Discounts**: Enjoy special deals and promotional discounts.
- **Loyalty Program**: Earn reward points through ticket purchases.
- **Discount Code Redemption**: Apply discount codes for better deals.
- **User Reviews & Feedback**: Share your experience and rate events.
- **Event Media Library**: Access images and videos after events.
- **Event Reminders & Notifications**: Stay updated with event alerts.
- **User Profile Management**: Customize your profile and preferences.
- **Artist & Event Tracking**: Follow favorite artists and get updates on their performances.

## Technologies Used
- **Frontend**: Android (Java/Kotlin)
- **Backend**: Node.js with Express.js
- **Database**: MongoDB
- **Payment Gateway**: MoMo (Test Mode), Stripe API

## Installation & Setup
1. Clone the repository:
   ```sh
   git clone https://github.com/KTPx4/Ticket_System.git
   cd Ticket_System
   ```
2. Install dependencies for the backend:
   ```sh
   npm install
   ```
3. Configure MoMo & Stripe payment gateway.
   - Install the MoMo test application.
   - Use the provided test credentials.

4. Install Database from `database` folder
   ```sh
   mongorestore --host localhost --port 27017 -d ChatDB <folder store database>
   ```
5. Run the backend server:
   ```sh
   npm start
   ```
6. Build and run the Android application in Android Studio.

## Test Credentials
### MoMo Test Account:
- **Phone Number**: 0938023111
- **OTP**: 0000
- **Password**: 000000

### Stripe Test Card:
- **Card Number**: 4242 4242 4242 4242
- **Expiration**: 02/26
- **CVC**: 222

### User Account:
- **Email**: kieuthanhphat.work@gmail.com
- **Password**: 12345

### Staff Account:
- **Username**: px4
- **Password**: px4.vnd@gmail.com

## Contributors
Developed by KTPx4.

## License
This project is licensed under the MIT License.

