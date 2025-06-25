# Carpooling Application

A Java/Swing desktop application for managing carpooling services with SQLite database integration.

## Overview

This application provides a complete carpooling management system with role-based access:
- **Passengers** can search, book, and manage trips
- **Drivers** can create and manage trips, and handle booking requests
- **Administrators** can manage users, trips, and system settings

## Features

- User registration and authentication
- Role-based dashboards for different user types
- Trip creation and management
- Trip search and booking system
- Recurring trips management
- User profile management
- Admin control panel
- **Real-time notification system** for trip requests and updates
- Notification panel with visual indicators and multiple notification types
- Test notification functionality for demonstration purposes

## Documentation

Comprehensive documentation is available in the `docs/` folder:
- [Analysis Report](docs/analysis-report.md)
- [Architecture Documentation](docs/architecture.md)
- [API Documentation](docs/api-documentation.md)
- [Installation Guide](docs/installation-guide.md)
- [User Manual](docs/user-manual.md)
- [SQLite Migration Guide](docs/sqlite-migration-guide.md)

## Technical Stack

- **Language:** Java 17
- **UI Framework:** Java Swing
- **Database:** SQLite (embedded)
- **Build Tool:** Apache Maven
- **Dependencies:**
  - SQLite JDBC Driver
  - LGoodDatePicker

## Quick Start

1. **Prerequisites**
   - JDK 17 or higher
   - Maven 3.8.x or higher

2. **Build**
   ```bash
   mvn clean package
   ```

3. **Run**
   ```bash
   java -jar target/carpooling-app-1.0-SNAPSHOT.jar
   ```

4. **Default Admin Account**
   - Email: admin@admin.com
   - Password: admin2025

## Testing the Notification System

The application includes a comprehensive notification system for real-time updates. You can test it in several ways:

1. **In the Application**
   - Login as a passenger
   - Use the "Test Notification" button to add a single test notification
   - Use the "Test Multiple Notifications" button to see different notification types
   - Click the bell icon to view and manage notifications

2. **Demo Script**
   ```bash
   mvn exec:java -Dexec.mainClass="com.carpooling.demo.NotificationDemo"
   ```

3. **Test Suite**
   ```bash
   mvn exec:java -Dexec.mainClass="com.carpooling.test.NotificationSystemTest"
   ```

### Notification Types
- **INFO** (Blue): General information and new trip announcements
- **SUCCESS** (Green): Trip requests accepted, successful actions
- **WARNING** (Orange): Important alerts and potential issues
- **ERROR** (Red): Trip cancellations, rejected requests

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.