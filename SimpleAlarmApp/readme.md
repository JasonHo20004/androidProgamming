# Simple Alarm Setter built by 22110020 - Ho Thanh Dat

## Overview
Simple Alarm Setter is a lightweight Android application that allows users to quickly set alarms on their device using the system's default alarm clock. The app features a clean, user-friendly interface that makes setting alarms straightforward and convenient.

## Features
- **Time Selection**: Choose alarm time using an intuitive time picker interface
- **Custom Messages**: Add personalized messages to your alarms
- **System Integration**: Works with your device's default alarm application
- **Real-time Preview**: See your selected time displayed in a readable format
- **24-hour Format Support**: Time picker supports both 12-hour and 24-hour formats

## Requirements
- Android 5.0 (Lollipop) or higher
- Device with a default alarm clock application

## Permissions
The application requires the following permission:
- `com.android.alarm.permission.SET_ALARM` - Allows the app to set alarms using the system's alarm clock

## How to Use
1. Clone the repository and open in Android Studio
2. Use the time picker to select your desired alarm time
3. Enter an optional custom message for your alarm
4. Tap the "Set Alarm" button
5. Confirm the alarm settings in your system's alarm app (if EXTRA_SKIP_UI is set to false)

## Development Notes
- Built with Kotlin
- Uses Android's AlarmClock intent system
- Implements Modern Android Development practices
- Follows Material Design guidelines for a consistent user experience

## Troubleshooting
If you encounter the "No alarm app available on this device" message, your device either doesn't have a default alarm clock app or it doesn't support the SET_ALARM intent action.

## Contributing
Feel free to fork this project and submit pull requests. For major changes, please open an issue first to discuss what you would like to change.

## License
[MIT](https://choosealicense.com/licenses/mit/)