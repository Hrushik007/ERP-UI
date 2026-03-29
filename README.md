# ERP System - Java Swing Application

A comprehensive Enterprise Resource Planning (ERP) system built with Java Swing for the course **Object Oriented Analysis and Design with Java**.

## Project Structure

```
ERP-UI/
├── src/
│   └── com/
│       └── erp/
│           ├── ERPApplication.java      # Main entry point
│           ├── model/                   # Data models (POJOs)
│           │   └── User.java
│           ├── view/                    # UI components
│           │   ├── LoginFrame.java
│           │   ├── MainFrame.java
│           │   ├── components/          # Reusable UI components
│           │   │   └── Sidebar.java
│           │   ├── panels/              # Module panels
│           │   │   ├── BasePanel.java   # Abstract base class
│           │   │   ├── DashboardPanel.java
│           │   │   └── PlaceholderPanel.java
│           │   └── dialogs/             # Dialog windows (future)
│           ├── controller/              # Controllers (future)
│           ├── service/                 # Business logic services
│           │   └── AuthenticationService.java
│           └── util/                    # Utilities
│               ├── Constants.java
│               └── UIHelper.java
├── out/                                 # Compiled classes (auto-generated)
├── run.bat                              # Windows run script
├── run.sh                               # Unix/Mac run script
└── README.md
```

## Prerequisites

- **Java Development Kit (JDK) 11 or higher**
  - Download from: https://adoptium.net/ (recommended) or https://www.oracle.com/java/technologies/downloads/

### Verify Java Installation
```bash
java -version
javac -version
```

Both commands should show version 11 or higher.

## Setup Instructions for VS Code

### Step 1: Install VS Code Extensions
1. Open VS Code
2. Go to Extensions (Ctrl+Shift+X)
3. Install these extensions:
   - **Extension Pack for Java** (by Microsoft) - Includes all necessary Java tools
   - OR install individually:
     - Language Support for Java (by Red Hat)
     - Debugger for Java (by Microsoft)
     - Java Test Runner (by Microsoft)

### Step 2: Configure Java in VS Code
1. Open VS Code Settings (Ctrl+,)
2. Search for "java.home"
3. Set the path to your JDK installation, e.g.:
   - Windows: `C:\\Program Files\\Eclipse Adoptium\\jdk-17.0.x-hotspot`
   - Mac: `/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home`

### Step 3: Open the Project
1. Open VS Code
2. File > Open Folder
3. Select the `ERP-UI` folder

## Running the Application

### Option 1: Using Command Line (Recommended for learning)

**Windows (Command Prompt or PowerShell):**
```cmd
cd C:\Users\suket\Desktop\ERP-UI
run.bat
```

**Windows (Git Bash or WSL):**
```bash
cd /c/Users/suket/Desktop/ERP-UI
./run.sh
```

### Option 2: Manual Compilation
```bash
# Navigate to project folder
cd C:\Users\suket\Desktop\ERP-UI

# Create output directory
mkdir out

# Compile all Java files
javac -d out src/com/erp/*.java src/com/erp/**/*.java src/com/erp/**/**/*.java

# Run the application
java -cp out com.erp.ERPApplication
```

### Option 3: VS Code Java Extension
1. Open `ERPApplication.java`
2. Click "Run" button above the `main` method
3. Or press F5 to debug

## Test Credentials

| Username | Password    | Role     |
|----------|-------------|----------|
| admin    | admin123    | Admin    |
| manager  | manager123  | Manager  |
| employee | emp123      | Employee |

## OOP Concepts Demonstrated

### Batch 1 (Current)

1. **Encapsulation** (`User.java`)
   - Private fields with public getters/setters
   - Controlled access to object state

2. **Abstraction** (`BasePanel.java`)
   - Abstract class defining common interface
   - Abstract methods for subclass implementation

3. **Inheritance** (`DashboardPanel.java`, `PlaceholderPanel.java`)
   - Extending BasePanel
   - Overriding abstract methods

4. **Polymorphism** (`MainFrame.java`)
   - Treating all panels as BasePanel type
   - Different behavior based on actual type

5. **Singleton Pattern** (`AuthenticationService.java`)
   - Single instance for authentication
   - Global access point

6. **Factory Method Pattern** (`MainFrame.createPanelForCommand()`)
   - Creates appropriate panel based on input
   - Encapsulates object creation

7. **Composition** (`MainFrame.java`, `Sidebar.java`)
   - Classes containing other classes
   - "Has-a" relationships

8. **Observer/Callback Pattern** (`Sidebar.java`, `LoginFrame.java`)
   - Event listeners for user actions
   - Loose coupling between components

## Modules (16 Total)

| # | Module | Status |
|---|--------|--------|
| 1 | Dashboard | ✅ Implemented |
| 2 | CRM | 🚧 Placeholder |
| 3 | Sales Management | 🚧 Placeholder |
| 4 | Order Processing | 🚧 Placeholder |
| 5 | Supply Chain | 🚧 Placeholder |
| 6 | Manufacturing | 🚧 Placeholder |
| 7 | Financial Management | 🚧 Placeholder |
| 8 | Accounting | 🚧 Placeholder |
| 9 | HR Management | 🚧 Placeholder |
| 10 | Project Management | 🚧 Placeholder |
| 11 | Reporting | 🚧 Placeholder |
| 12 | Data Analytics | 🚧 Placeholder |
| 13 | Business Intelligence | 🚧 Placeholder |
| 14 | Marketing | 🚧 Placeholder |
| 15 | Automation | 🚧 Placeholder |
| 16 | Integration | 🚧 Placeholder |

## Development Roadmap

- **Batch 1** (Current): Foundation - Login, Dashboard, Navigation
- **Batch 2**: HR Management Module
- **Batch 3**: Sales & Order Processing
- **Batch 4**: Financial & Accounting
- **Batch 5**: CRM Module
- **Batch 6**: Supply Chain & Manufacturing
- **Batch 7**: Reporting & Analytics
- **Batch 8**: Marketing & Automation
- **Batch 9**: Business Intelligence & Integration
- **Batch 10**: Final integration and polish

## Version History

- **v1.0.0** - Initial batch: Foundation architecture, Login, Dashboard, Navigation framework
