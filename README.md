# PragamentTech-Internship-Assignment
Android Resume App – An interactive Android application that fetches resume data via API and displays it in a customizable card. Users can adjust font size, font and background colors, and view real-time location using Google Location Services. Built with Java, XML, and Retrofit.

## 📱 Features

- **Dynamic Resume Fetching**  
  Fetches resume data from an API (using Retrofit) and displays:
  - Personal Info
  - Summary
  - Skills
  - Projects

- **Customization Options**
  - Adjustable font size via SeekBar
  - Choose font color and background color
  - Real-time updates in preview card

- **Location Integration**
  - Retrieves live GPS coordinates using FusedLocationProviderClient
  - Displays latitude & longitude in the app

- **User-Friendly UI**
  - Resume displayed inside a clean CardView
  - Material Design buttons & dialogs
  - Error handling for network failures

## 🛠️ Tech Stack

- **Language**: Java  
- **UI**: XML layouts + Material Design  
- **Networking**: Retrofit  
- **Location Services**: Google Play Services Location API  
- **Architecture**: Activity-based with model classes  

## 📸 Screenshots

<!-- <img src="https://github.com/jeevandeepsaini/PragamentTech-Internship-Assignment/blob/main/CustomResumeGen.jpg" alt="App Screenshot" width="250"/> -->

## 🚀 How to Run

1. Clone this repository:
   ```bash
   git clone https://github.com/your-username/resume-generator-app.git
2. Open in **Android Studio**.
3. Add your API endpoint in `ResumeApi.java`
4. Run the app on an emulator or device.

## 📄 License

This project is licensed under the [MIT License](https://github.com/jeevandeepsaini/PragamentTech-Internship-Assignment/blob/main/LICENSE)
