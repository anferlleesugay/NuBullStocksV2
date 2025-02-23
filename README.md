# NUBULLSTOCKS Shopping App

## Overview
NUBULLSTOCKS is a shopping app developed for a school project. The app allows users to browse products, add them to the cart, place pre-orders, and track stock availability. Admin users can manage products, receive pre-order notifications, and handle other app functionalities.

CREATED BY: ANFERL LEE SUGAY & MERNER MAGTOTO

## Features
- **User Features:**
  - View product details (name, image, stock availability)
  - Add products to the cart
  - Pre-order products when out of stock
  - View product images and descriptions
- **Admin Features:**
  - Manage product inventory (add, update, delete products)
  - View and manage pre-order requests
  - Send notifications for product availability and pre-order status

## Tech Stack
- **Frontend**: Android (Kotlin)
- **Backend**: Firebase (for authentication, product data, and pre-orders)
- **Cloud Storage**: AWS (for product images)
- **UI**: ConstraintLayout for responsive design

## Requirements
- Android Studio
- Firebase account
- AWS account
- Kotlin 1.5+ (used for Android development)
- API 35 (Android 15.0) for compatibility

## Setup Instructions
1. Clone the repository to your local machine:
    ```bash
    git clone https://github.com/yourusername/nubullstocks.git
    ```

2. Open the project in Android Studio.

3. Add Firebase to the project:
    - Follow the Firebase setup guide: https://firebase.google.com/docs/android/setup
    - Add your `google-services.json` file to the `app/` directory.

4. Set up AWS for storing product images:
    - Create an S3 bucket in AWS for storing images.
    - Update your app’s AWS credentials in `strings.xml` or use AWS SDK.

5. Build and run the project on an Android device or emulator.

## Screenshots
Here are some screenshots of the app's UI:

- Product Details Screen
- Admin Dashboard
- Cart and Pre-order functionality

*(You can add images here once you take some screenshots)*

## Contributions
Feel free to contribute to this project by submitting issues, making pull requests, or suggesting new features. To contribute:
1. Fork the repository
2. Create a new branch (`git checkout -b feature-name`)
3. Commit your changes (`git commit -am 'Add new feature'`)
4. Push to the branch (`git push origin feature-name`)
5. Create a new Pull Request

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact
For any inquiries or feedback, feel free to contact me at:
- Email: alees2604@gmail.com
- GitHub: [anferlleesugay](https://github.com/anferlleesugay)
