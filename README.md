# ERP-UI - Tata Motors Enterprise Resource Planning (Swing)

This repository contains a Java Swing ERP UI with 15 navigable modules, implemented using a layered OOAD style (UI -> service -> subsystem SDK).

Team UI - AIML 'B' Section 
- Hrushik M Hegde (PES1UG23AM118)
- Harshavardhan M (PES1UG23AM112)
- Havyshmathy V   (PES1UG23AM116)
- Arpita R        (PES1UG23AM067)

## 1. Integration Status

### 1.1 Module status at a glance

- Fully implemented integration logic: Manufacturing
- Authentication implemented with mock adapter: Login + MockUIAuthenticator
- UI shell fully implemented: LoginFrame, MainFrame, Sidebar, PanelRegistry, BasePanel
- Placeholder/facade UI modules: most Orders, HR, Supply Chain tabs, plus facade modules (CRM, Finance, Sales, etc.)

### 1.2 Modules and tab structure

| Module | Top-level implementation status | Tabs |
|---|---|---|
| Dashboard | UI implemented | Executive Dashboard |
| Order Processing | Mostly placeholder tabs | Dashboard, Orders, Inventory, Reports |
| HR Management | Placeholder tabs | Employee Info, Recruitment, Onboarding, Payroll, Attendance & Leave, Performance, Workforce Planning, Benefits |
| Manufacturing | Deep integration with BOMService + SDK + API server/client | Assembly Lines, Production Orders, BOM Explorer, Routing, Work Centers, Quality Control, Planning, Shop Floor |
| Supply Chain | Placeholder tabs (UI stubs) | Dashboard, Inventory, Purchase Orders, Suppliers, Goods Receipts, Shipments, Invoices, Requisitions |
| Automation | Placeholder tabs via stubs | Dashboard, Workflows, Rules Engine |
| CRM/Sales/Finance/Accounting/Project/Reporting/Analytics/BI/Marketing | Facade placeholder panels | Stub tabs |

## 2. Actual Architecture (from source)

### 2.1 Runtime flow

1. ERPApplication.main initializes look-and-feel and launches LoginFrame on EDT.
2. LoginFrame authenticates via UIAuthenticator (currently MockUIAuthenticator).
3. MainFrame receives authenticated user and renders shell (header + sidebar + content cards).
4. Sidebar command is routed to PanelRegistry.create(command).
5. Created panel (BasePanel subclass) is cached, initialized lazily via ensureInitialized(), then refreshed.

Primary classes:
- src/com/erp/ERPApplication.java
- src/com/erp/view/LoginFrame.java
- src/com/erp/view/MainFrame.java
- src/com/erp/view/PanelRegistry.java
- src/com/erp/view/panels/BasePanel.java

### 2.2 Manufacturing subsystem integration (UI team + Manufacturing team)

Manufacturing is the strongest real integration in this codebase.

Key integration points:
- BOMService loads subsystem SDK using SubsystemFactory + DatabaseConfig from src/main/resources/application-rds.properties.
- BOMService maps raw Map<String,Object> data into domain models (BOM, Material, RoutingStep, ProductionOrder, etc.).
- ManufacturingHomePanel hosts the module tabs and starts InventoryApiServer.
- InventoryApiServer exposes /api/inventory/materials (GET/POST/PUT) for subsystem communication.
- InventoryApiClient pushes new materials to external Supply Chain endpoint.

Key files:
- src/com/erp/service/BOMService.java
- src/com/erp/service/InventoryApiServer.java
- src/com/erp/service/InventoryApiClient.java
- src/com/erp/view/panels/manufacturing/ManufacturingHomePanel.java
- src/com/erp/view/panels/manufacturing/BOMExplorerTab.java
- src/com/erp/view/panels/manufacturing/ManufacturingPlanningTab.java
- src/com/erp/view/panels/manufacturing/ShopFloorExecutionTab.java
- src/com/erp/view/panels/manufacturing/QualityControlTab.java

### 2.3 Exception model currently present

Manufacturing-specific checked exceptions are defined as direct Exception subclasses:
- CapacityOverloadException
- DuplicateBomVersionException
- InvalidBomStructureException
- InvalidQuantityInputException
- ProductionOrderCancellationBlockedException
- QcDefectThresholdExceededException
- RoutingStepSequenceGapException

Path:
- src/com/erp/exceptions/

## 3. SOLID Principles Used

The table below lists SOLID principles that are visible in the current implementation and where they are applied.

| Principle | How it is used in this codebase | Where |
|---|---|---|
| Single Responsibility Principle (SRP) | UI shell, navigation, utility styling, auth contract, and manufacturing data orchestration are separated into focused classes. | MainFrame, Sidebar, UIHelper, UIAuthenticator, BOMService |
| Open/Closed Principle (OCP) | New module panels can be added by registering a new factory in PanelRegistry without changing MainFrame navigation logic. | src/com/erp/view/PanelRegistry.java, src/com/erp/view/MainFrame.java |
| Liskov Substitution Principle (LSP) | All module panels are substitutable BasePanel implementations; MainFrame treats them uniformly through BasePanel API (ensureInitialized, refreshData, getPanelTitle). | src/com/erp/view/panels/BasePanel.java and all subclasses |
| Interface Segregation Principle (ISP) | Interfaces are split by concern: UIAuthenticator for login, and separate HRService/CRMService/SalesService/FinanceService contracts instead of one monolithic interface. | src/com/erp/service/UIAuthenticator.java, HRService.java, CRMService.java, SalesService.java, FinanceService.java |
| Dependency Inversion Principle (DIP) | High-level UI depends on abstractions and facade services. Login uses UIAuthenticator interface. BOMService depends on subsystem abstraction via SDK factory instead of embedding SQL in UI tabs. | src/com/erp/view/LoginFrame.java, src/com/erp/service/UIAuthenticator.java, src/com/erp/service/BOMService.java |

## 4. GRASP Principles Used

| GRASP Principle | How it is used | Where |
|---|---|---|
| Information Expert | BOMService owns manufacturing read/write operations and mapping because it has the required subsystem and schema knowledge. | src/com/erp/service/BOMService.java |
| Creator | UIHelper creates styled Swing controls; PanelRegistry creates module panels; both are natural creators for those objects. | src/com/erp/util/UIHelper.java, src/com/erp/view/PanelRegistry.java |
| Controller | BOMService handles manufacturing-related system operations requested by UI tabs (plans, orders, routing, QC, shop-floor logs). | src/com/erp/service/BOMService.java |
| Low Coupling | UI tabs call service APIs instead of direct DB operations; MainFrame delegates object creation to PanelRegistry; auth is behind UIAuthenticator. | manufacturing tabs, MainFrame, PanelRegistry, LoginFrame |
| High Cohesion | Each panel/tab class has a focused responsibility (for example BOMExplorerTab for BOM visualization, ShopFloorExecutionTab for execution logging). | src/com/erp/view/panels/manufacturing/*.java |
| Polymorphism | BasePanel defines abstract lifecycle methods and behavior is specialized via subclass overrides. | src/com/erp/view/panels/BasePanel.java and module panel subclasses |
| Pure Fabrication | Utility and registry classes are introduced to keep domain/UI classes cleaner and more cohesive. | src/com/erp/util/UIHelper.java, src/com/erp/view/PanelRegistry.java, src/com/erp/util/JSONUtil.java |
| Indirection | PanelRegistry mediates command-to-panel creation, reducing direct dependency between MainFrame and concrete modules. | src/com/erp/view/PanelRegistry.java |
| Protected Variations | Interface boundaries protect change points: UIAuthenticator implementation can be replaced, service interfaces can be implemented per team, panel registration can vary without rewriting shell logic. | src/com/erp/service/UIAuthenticator.java, src/com/erp/service/*Service.java, src/com/erp/view/PanelRegistry.java |

## 5. Design Patterns Used (OOAD-Oriented)

| Pattern | Type | How/where it is used |
|---|---|---|
| MVC (architectural style) | Architectural | Models in src/com/erp/model, views in src/com/erp/view/panels, and service/controller-style coordination in src/com/erp/service (especially BOMService). |
| Template Method | Behavioral | BasePanel defines panel lifecycle skeleton (header + content shell + deferred initialize/layout) and subclasses fill details. |
| Factory Method | Creational | UIHelper factory methods create consistently styled controls (createPrimaryButton, createSecondaryButton, etc.). |
| Registry + Factory | Creational/Structural | PanelRegistry stores command-to-factory mapping and instantiates panels on demand. |
| Singleton | Creational | BOMService and InventoryApiServer use singleton accessors (getInstance) for shared lifecycle/state. |
| Facade | Structural | BOMService provides a simplified API over subsystem SDK operations and mapping complexity. |
| Adapter | Structural | InventoryApiServer.MaterialsHandler adapts HTTP payloads to BOMService methods; BOMService maps generic maps to typed model objects. |
| Composite | Structural | JTabbedPane-based module composition and recursive BOMNode tree representation in JSONUtil/BOMExplorerTab. |
| Observer (event/listener) | Behavioral | Swing listeners (ActionListener, ChangeListener, ListSelectionListener, DocumentListener) drive UI updates and command handling. |
| Strategy | Behavioral | TableRowSorter with dynamic RowFilter in ShopFloorExecutionTab enables runtime filtering strategy. |
| DTO/Data Model | Enterprise/Structural | Plain model classes (BOM, Material, ProductionOrder, etc.) transfer data between service layer and UI. |
| Proxy-style integration client | Structural | InventoryApiClient wraps remote HTTP call details for Supply Chain synchronization. |

## 6. Manufacturing Integration Workflow

Current workflow implemented by UI + service code:

1. Material management:
- Create materials via AddMaterialDialog -> BOMService.addMaterial
- Push material update to Supply Chain via InventoryApiClient

2. BOM management:
- Create/update BOM in BOMExplorerTab/NewBOMDialog
- JSONUtil serializes/deserializes hierarchical BOM content

3. Planning and orders:
- Create production plans and convert to orders through ManufacturingPlanningTab + BOMService

4. Assembly routing and execution:
- Manage line assignments and movements in AssemblyLinesTab
- Log output quantities in ShopFloorExecutionTab

5. Quality control:
- Submit QC in QualityControlTab
- Defect threshold rule enforced in BOMService.logQualityCheck

6. Cross-subsystem API bridge:
- InventoryApiServer exposes material endpoints for inbound synchronization

## 7. Source Layout

Core package layout:

- src/com/erp/ERPApplication.java
- src/com/erp/view/
- src/com/erp/view/components/
- src/com/erp/view/panels/
- src/com/erp/view/panels/manufacturing/
- src/com/erp/view/panels/orders/
- src/com/erp/view/panels/hr/
- src/com/erp/view/panels/supplychain/
- src/com/erp/view/panels/facade/
- src/com/erp/service/
- src/com/erp/model/
- src/com/erp/util/
- src/com/erp/exceptions/

## 8. Build and Run

Requirements:
- JDK 11+
- ERP subsystem SDK JAR(s) in lib/

Options:

Windows batch build/run:
- run.bat

Python cross-platform build/run script:
- run.py

Manual compile/run example (Windows classpath style):

javac -cp .;lib/* -d out @sources.txt
java -cp out;lib/* com.erp.ERPApplication

## 9. Integration Notes for Subsystem Teams

- Auth is abstracted through UIAuthenticator; replace MockUIAuthenticator with real DB-backed implementation when ready.
- Manufacturing integration already consumes subsystem SDK and DB config through BOMService.
- Orders, HR, and Supply Chain UI currently contain many stub tabs that are ready for backend wiring.
- PanelRegistry is the extension seam for adding or replacing module panels.

## 10. Current Scope Clarification

This README reflects the code currently present in this repository. It intentionally does not claim classes/contracts that are not in src/com/erp.

If additional integration components exist only in external JARs under lib/, they should be documented in a separate SDK contract document and linked here.
