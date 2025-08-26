package com.example.cafe;

import java.util.Scanner;
import com.example.cafe.loyalty.Discount;
import com.example.cafe.loyalty.FreeCoffee;
import com.example.cafe.loyalty.FreeDonates;
import com.example.cafe.loyalty.FreeIceCream;
import com.example.cafe.loyalty.FreePizza;
import com.example.cafe.loyalty.FreeWaffel;
import com.example.cafe.loyalty.ILoyaltyProgram;
import com.example.cafe.loyalty.IredeemPoints;
import com.example.cafe.loyalty.LoyaltyProgram;
import com.example.cafe.menu.IMenuItem;
import com.example.cafe.menu.IMenuManager;
import com.example.cafe.menu.MenuItem;
import com.example.cafe.menu.MenuItemCategory;
import com.example.cafe.menu.MenuManager;
import com.example.cafe.notification.INotificationSystem;
import com.example.cafe.notification.NotificationSystem;
import com.example.cafe.order.IOrder;
import com.example.cafe.order.IOrderProcessor;
import com.example.cafe.order.Order;
import com.example.cafe.order.OrderProcessor;
import com.example.cafe.payment.CashPayment;
import com.example.cafe.payment.CreditCardPayment;
import com.example.cafe.payment.MobilePayment;
import com.example.cafe.payment.PaymentSystem;
import com.example.cafe.payment.VisaPayment;
import com.example.cafe.report.IReportManager;
import com.example.cafe.report.ReportManager;
import com.example.cafe.staff.IStaff;
import com.example.cafe.staff.Staff;
import com.example.cafe.student.IStudent;
import com.example.cafe.student.StudentManager;
import com.example.cafe.student.StudentOrderList;
import com.example.cafe.userManager.IUserManager;

public class RestaurantSystem {
    public void startSystem() {
        // Initialize order ID from file before anything else
        com.example.cafe.order.Order.initializeOrderIdFromFile("src/dataFiles/orders.txt");
        Scanner scanner = new Scanner(System.in);
        IUserManager<IStudent> studentManager = new StudentManager();
        IMenuManager menuManager = new MenuManager();
        INotificationSystem notificationSystem = new NotificationSystem();
        ILoyaltyProgram loyaltyProgram = new LoyaltyProgram();
        StudentOrderList studentOrderList = new StudentOrderList();
        IOrderProcessor orderProcessor = new OrderProcessor(loyaltyProgram, studentOrderList);
        IStaff staff = new Staff(orderProcessor, notificationSystem);
        IReportManager reportManager = new ReportManager(orderProcessor, loyaltyProgram);

        // PIZZA
        menuManager.addMenuItem(new MenuItem("Cheese Pizza", "Classic cheese pizza", 50.0, MenuItemCategory.PIZZA));
        menuManager.addMenuItem(
                new MenuItem("Pepperoni Pizza", "Pepperoni and cheese pizza", 55.0, MenuItemCategory.PIZZA));

        // SANDWICHES
        menuManager.addMenuItem(
                new MenuItem("Chicken Sandwich", "Grilled chicken sandwich", 35.0, MenuItemCategory.SANDWICHES));
        menuManager.addMenuItem(
                new MenuItem("Tuna Sandwich", "Tuna and mayo sandwich", 32.0, MenuItemCategory.SANDWICHES));

        // SNACKS
        menuManager.addMenuItem(new MenuItem("French Fries", "Crispy french fries", 18.0, MenuItemCategory.SNACKS));
        menuManager.addMenuItem(new MenuItem("Onion Rings", "Fried onion rings", 20.0, MenuItemCategory.SNACKS));

        // HOT_DRINKS
        menuManager.addMenuItem(new MenuItem("Espresso", "Hot espresso coffee", 15.0, MenuItemCategory.HOT_DRINKS));
        menuManager.addMenuItem(
                new MenuItem("Cappuccino", "Cappuccino with milk foam", 18.0, MenuItemCategory.HOT_DRINKS));

        // COLD_DRINKS
        menuManager.addMenuItem(new MenuItem("Iced Coffee", "Cold iced coffee", 17.0, MenuItemCategory.COLD_DRINKS));
        menuManager.addMenuItem(new MenuItem("Lemonade", "Fresh lemonade", 12.0, MenuItemCategory.COLD_DRINKS));

        // ICECREAM
        menuManager.addMenuItem(
                new MenuItem("Vanilla Ice Cream", "Classic vanilla ice cream", 20.0, MenuItemCategory.ICECREAM));
        menuManager.addMenuItem(
                new MenuItem("Chocolate Ice Cream", "Rich chocolate ice cream", 22.0, MenuItemCategory.ICECREAM));

        // DESSERTS
        menuManager.addMenuItem(
                new MenuItem("Belgian Waffle", "Belgian waffle with syrup", 30.0, MenuItemCategory.DESSERTS));
        menuManager
                .addMenuItem(new MenuItem("Chocolate Cake", "Chocolate layered cake", 28.0, MenuItemCategory.DESSERTS));

        boolean running = true;

        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║      Welcome to the University Restaurant!         ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");

        while (running) {
            System.out.println("╔════════════════════════════════╗");
            System.out.println("║         LOGIN OPTIONS          ║");
            System.out.println("╠════════════════════════════════╣");
            System.out.println("║ 1. Register as a new student   ║");
            System.out.println("║ 2. Login as student            ║");
            System.out.println("║ 3. Login as admin              ║");
            System.out.println("║ 0. Exit                        ║");
            System.out.println("╚════════════════════════════════╝");
            int mainChoice = -1;
            boolean validInput = false;
            while (!validInput) {
                System.out.print("Your choice: ");
                try {
                    mainChoice = scanner.nextInt();
                    scanner.nextLine();
                    validInput = true;
                } catch (Exception e) {
                    System.out.println("Please enter a valid number!");
                    scanner.nextLine(); // clear invalid input
                }
            }

            switch (mainChoice) {
                case 1: // Register new student
                    newStudentRegister(scanner, studentManager);
                    break;

                case 2: // Login as student
                    LoginAsStudent(scanner, studentManager, menuManager, orderProcessor, loyaltyProgram,
                            notificationSystem,
                            reportManager);
                    break;

                case 3: // Login as admin
                    LoginAsAdmin(scanner, staff, menuManager, reportManager);
                    break;

                case 0: // Exit
                    running = exitProgram();
                    break;

                default:
                    System.out.println("Invalid option.");
            }
        }

        scanner.close();
    }

    private static void newStudentRegister(Scanner scanner, IUserManager<IStudent> studentManager) {
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter student ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter password: ");
        String pass = scanner.nextLine();
        if (studentManager.register(name, id, pass)) {
            System.out.println("Registration successful!");
        } else {
            System.out.println("Student already exists.");
        }
    }

    private static void LoginAsStudent(Scanner scanner, IUserManager<IStudent> studentManager, IMenuManager menuManager,
            IOrderProcessor orderProcessor, ILoyaltyProgram loyaltyProgram, INotificationSystem notificationSystem,
            IReportManager reportManager) {
        IStudent currentStudent;
        System.out.print("Enter student ID: ");
        String loginId = scanner.nextLine();
        System.out.print("Enter password: ");
        String loginPass = scanner.nextLine();
        if (studentManager.login(loginId, loginPass)) {
            currentStudent = studentManager.getLoggedInUser();
            System.out.println("\n╔════════════════════════════════════════════════════╗");
            System.out.print("║   🎉 Login successful!                             ║\n");
            System.out.printf("║   Welcome, %-20s                    ║\n", currentStudent.getName());
            System.out.println("║   We are happy to have you at the University       ║");
            System.out.println("║   Restaurant!                                      ║");
            System.out.println("╚════════════════════════════════════════════════════╝\n");
            boolean studentSession = true;

            while (studentSession) {
                System.out.println("\nStudent options:");
                System.out.println("1. Show menu");
                System.out.println("2. Place new order");
                System.out.println("3. Pay for order");
                System.out.println("4. Redeem loyalty points");
                System.out.println("5. Show notifications");
                System.out.println("6. Show loyalty points");
                System.out.println("7. Show my orders");
                System.out.println("0. Logout");
                int studentChoice = -1;
                boolean validStudentInput = false;
                while (!validStudentInput) {
                    System.out.print("Your choice: ");
                    try {
                        studentChoice = scanner.nextInt();
                        scanner.nextLine();
                        validStudentInput = true;
                    } catch (Exception e) {
                        System.out.println("Please enter a valid number!");
                        scanner.nextLine();
                    }
                }

                switch (studentChoice) {
                    case 1: // Show menu
                        showMenu(menuManager);
                        break;

                    case 2: // Place new order
                        placeOrder(scanner, menuManager, orderProcessor, currentStudent);
                        break;

                    case 3: // Pay for order
                        payForOrder(scanner, orderProcessor, currentStudent, reportManager);
                        break;

                    case 4: // Redeem loyalty points
                        redeemPoints(scanner, loyaltyProgram, currentStudent);
                        break;

                    case 5: // Show notifications
                        viewNotifications(notificationSystem);
                        break;

                    case 6: // Show loyalty points
                        showLoyaltyPoints((LoyaltyProgram) loyaltyProgram, currentStudent);
                        break;

                    case 7: // Show my orders
                        showStudentOrders(orderProcessor, currentStudent);
                        break;

                    case 0: // Logout
                        studentSession = false;
                        studentManager.logout();
                        currentStudent = null;
                        System.out.println("Logged out.");
                        break;

                    default:
                        System.out.println("Invalid option.");
                }

            }
        } else {
            System.out.println("Login failed.");
        }
    }

    private static void showLoyaltyPoints(LoyaltyProgram loyaltyProgram, IStudent currentStudent) {
        int currentPoints = 0;
        try {
            currentPoints = loyaltyProgram.getLoyaltyPoints(currentStudent);
        } catch (Exception e) {
            System.out.println("Error reading loyalty points: " + e.getMessage());
        }
        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║      LOYALTY POINTS          ║");
        System.out.println("╚══════════════════════════════╝");
        System.out.println("\n==============================================");
        System.out.println("You currently have " + currentPoints + " loyalty points.");
        System.out.println("==============================================");

    }

    private static void LoginAsAdmin(Scanner scanner, IStaff staff, IMenuManager menuManager,
            IReportManager reportManager) {
        System.out.print("Enter admin username: ");
        String adminUser = scanner.nextLine();
        System.out.print("Enter admin password: ");
        String adminPass = scanner.nextLine();
        if (staff.login(adminUser, adminPass)) {
            // System.out.println("Admin login successful!");
            System.out.println("\n╔════════════════════════════════════════════════════╗");
            System.out.print("║   🎉 Login successful!                             ║\n");
            System.out.print("║   Welcome, Admin                                   ║\n");
            System.out.println("╚════════════════════════════════════════════════════╝\n");

            boolean adminSession = true;

            while (adminSession) {
                System.out.println("\nAdmin options:");
                System.out.println("1. Add menu item");
                System.out.println("2. Edit menu item");
                System.out.println("3. Remove menu item");
                System.out.println("4. Show total sales");
                System.out.println("5. Show loyalty redemptions report");
                System.out.println("6. Show pending orders");
                System.out.println("7. View menu");
                System.out.println("8. Mark order as preparing");
                System.out.println("9. Mark order as ready");
                System.out.println("0. Logout");
                int adminChoice = -1;
                boolean validAdminInput = false;
                while (!validAdminInput) {
                    System.out.print("Your choice: ");
                    try {
                        adminChoice = scanner.nextInt();
                        scanner.nextLine();
                        validAdminInput = true;
                    } catch (Exception e) {
                        System.out.println("Please enter a valid number!");
                        scanner.nextLine();
                    }
                }

                switch (adminChoice) {
                    case 1: // Add menu item
                        addItemToMenu(scanner, menuManager);
                        break;

                    case 2: // Edit menu item
                        editItemInMenu(scanner, menuManager);
                        break;

                    case 3: // Remove menu item
                        removeItemFromMenu(scanner, menuManager);
                        break;

                    case 4: // Show total sales
                        showTotalSales(reportManager);
                        break;

                    case 5: // Show loyalty redemptions report
                        showLoyaltyRedemptionsReport(reportManager);
                        break;

                    case 6: // Show pending orders
                        showPendingOrders(staff);
                        break;

                    case 7: // View menu
                        showMenu(menuManager);
                        break;

                    case 8: // Mark order as preparing
                        markOrderAsPreparing(scanner, staff);
                        break;

                    case 9: // Mark order as ready
                        markOrderAsReady(scanner, staff);
                        break;

                    case 0: // Logout
                        adminSession = false;
                        staff.logout();
                        System.out.println("Logged out.");
                        break;

                    default:
                        System.out.println("Invalid option.");
                }
            }
        } else {
            System.out.println("Admin credentials are incorrect.");
        }
    }

    // ============================================StudentMethods==========================================
    private static void showMenu(IMenuManager menuManager) {
        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║           MENU               ║");
        System.out.println("╚══════════════════════════════╝");
        java.util.Map<MenuItemCategory, java.util.List<IMenuItem>> categoryMap = menuManager.getTheMenu().stream()
                .filter(item -> item instanceof MenuItem)
                .collect(java.util.stream.Collectors.groupingBy(item -> ((MenuItem) item).getCategory()));
        categoryMap.forEach((category, items) -> {
            String categoryName = (category != null) ? category.name() : "Other";
            System.out.println("\n--- " + categoryName + " ---");
            items.forEach(item -> {
                System.out.printf("%-20s | %6.2f EGP | %s\n", item.getName(), item.getPrice(), item.getDescription());
            });
        });
        System.out.println();
    }

    private static void placeOrder(Scanner scanner, IMenuManager menuManager, IOrderProcessor orderProcessor,
            IStudent currentStudent) {
        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║        PLACE ORDER           ║");
        System.out.println("╚══════════════════════════════╝");
        IOrder order = new Order(currentStudent.getStudentId());
        java.util.List<IMenuItem> menuItems = menuManager.getTheMenu();
        while (true) {
            System.out.println("Menu:");
            for (int i = 0; i < menuItems.size(); i++) {
                IMenuItem item = menuItems.get(i);
                System.out.printf("%2d. %-20s | %6.2f EGP | %s\n", (i + 1), item.getName(), item.getPrice(),
                        item.getDescription());
            }
            int itemNumber = -1;
            boolean validItemInput = false;
            while (!validItemInput) {
                System.out.print("Enter the item number you want to add (or 0 to finish): ");
                try {
                    itemNumber = scanner.nextInt();
                    scanner.nextLine();
                    validItemInput = true;
                } catch (Exception e) {
                    System.out.println("Please enter a valid number!");
                    scanner.nextLine();
                }
            }
            if (itemNumber == 0) {
                break;
            }
            if (itemNumber < 1 || itemNumber > menuItems.size()) {
                System.out.println("Invalid item number.\n");
                continue;
            }
            IMenuItem selectedItem = menuItems.get(itemNumber - 1);
            int quantity = -1;
            boolean validQuantityInput = false;
            while (!validQuantityInput) {
                System.out.print("Enter the quantity: ");
                try {
                    quantity = scanner.nextInt();
                    scanner.nextLine();
                    validQuantityInput = true;
                } catch (Exception e) {
                    System.out.println("Please enter a valid number!");
                    scanner.nextLine();
                }
            }
            if (quantity < 1) {
                System.out.println("Invalid quantity.\n");
                continue;
            }
            for (int i = 0; i < quantity; i++) {
                order.addItemToOrder(selectedItem);
            }
            System.out.println("==============================================");
            System.out.println("Added " + quantity + " x " + selectedItem.getName() + " to your order.");
            System.out.println("==============================================");
        }

        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║       ORDER SUMMARY          ║");
        System.out.println("╚══════════════════════════════╝");
        java.util.Map<String, Long> itemCount = order.getOrderMenuItemList().getOrderMenuItems().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        omi -> omi.getMenuItem().getName(), java.util.stream.Collectors.counting()));
        itemCount.forEach((name, count) -> {
            System.out.printf("- %-20s x %d\n", name, count);
        });
        System.out.printf("Total price: %,.2f EGP\n", order.getTotalPrice());
        System.out.println("==============================================");
        System.out.print("Do you want to confirm this order? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (confirm.equals("yes")) {
            orderProcessor.placeOrder(order, currentStudent);
            System.out.println("==============================================");

            System.out.println("\n✔ Order placed successfully! Your Order ID is: " + order.getOrderId() + "\n");
            System.out.println("==============================================");

        } else {
            System.out.println("Order cancelled.\n");
        }
    }

    private static void payForOrder(Scanner scanner, IOrderProcessor orderProcessor, IStudent currentStudent,
            IReportManager reportManager) {
        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║         PAYMENT              ║");
        System.out.println("╚══════════════════════════════╝");
        int payOrderId = -1;
        boolean validOrderIdInput = false;
        while (!validOrderIdInput) {
            System.out.print("Enter order ID to pay: ");
            try {
                payOrderId = scanner.nextInt();
                scanner.nextLine();
                validOrderIdInput = true;
            } catch (Exception e) {
                System.out.println("Please enter a valid number!");
                scanner.nextLine();
            }
        }
        IOrder payOrder = orderProcessor.getOrderById(payOrderId);
        if (payOrder == null) {
            System.out.println("Order not found.\n");
            return;
        }
        if (payOrder.isPaid()) {
            System.out.println("==============================================");
            System.out.println("Order is already paid. You cannot pay again.");
            System.out.println("==============================================");
            return;
        }

        double originalTotal = payOrder.getTotalPrice();
        double discountedTotal = originalTotal;
        ILoyaltyProgram loyaltyProgram = null;
        if (orderProcessor instanceof OrderProcessor) {
            loyaltyProgram = ((OrderProcessor) orderProcessor).getLoyaltyProgram();
        }
        if (loyaltyProgram != null && currentStudent != null) {
            int currentPoints = loyaltyProgram.getLoyaltyPoints(currentStudent);
            System.out.printf("Order total: %,.2f EGP\n", originalTotal);
            System.out.println("You have " + currentPoints + " loyalty points.");
            System.out.print("Do you want to redeem points for a discount? (yes/no): ");
            String redeemAns = scanner.nextLine().trim().toLowerCase();
            if (redeemAns.equals("yes")) {
                System.out.println("Choose discount option:");
                System.out.println("1. 10 EGP discount (50 points)");
                System.out.println("2. 20 EGP discount (100 points)");
                System.out.println("3. 40 EGP discount (200 points)");
                int discountChoice = -1;
                boolean validDiscountInput = false;
                while (!validDiscountInput) {
                    System.out.print("Your choice: ");
                    try {
                        discountChoice = scanner.nextInt();
                        scanner.nextLine();
                        validDiscountInput = true;
                    } catch (Exception e) {
                        System.out.println("Please enter a valid number!");
                        scanner.nextLine();
                    }
                }
                Discount discountStrategy = null;
                switch (discountChoice) {
                    case 1:
                        discountStrategy = new Discount(10, 50);
                        break;
                    case 2:
                        discountStrategy = new Discount(20, 100);
                        break;
                    case 3:
                        discountStrategy = new Discount(40, 200);
                        break;
                    default:
                        System.out.println("Invalid discount option. Skipping discount.");
                }
                if (discountStrategy != null) {
                    if (discountStrategy.redeemPoints(currentStudent, loyaltyProgram)) {
                        discountedTotal = Math.max(0, originalTotal - discountStrategy.getDiscountAmount());
                        System.out.printf("Discount applied: -%d EGP\n", discountStrategy.getDiscountAmount());
                        System.out.printf("New total: %,.2f EGP\n", discountedTotal);
                    } else {
                        System.out.println("Not enough points for selected discount. Skipping discount.");
                    }
                }
            }
        }

        if (discountedTotal == 0.0) {
            payOrder.setPaid(true);

            if (orderProcessor instanceof OrderProcessor) {
                ((OrderProcessor) orderProcessor).payOrder(payOrder.getOrderId());
            }
            reportManager.addToTotalSales(0.0);
            System.out.println("==============================================");
            System.out.println("\n✔ Order fully paid by points!\n");
            System.out.println("==============================================");
            try {
                if (loyaltyProgram != null && currentStudent != null) {
                    loyaltyProgram.awardPoints(currentStudent, 0.0); // No points for free order
                }
            } catch (Exception e) {
                System.out.println("Error awarding loyalty points: " + e.getMessage());
            }
            return;
        }

        System.out.println("Choose payment method:");
        System.out.println("1. Cash");
        System.out.println("2. Credit Card");
        System.out.println("3. Mobile");
        System.out.println("4. Visa");
        int payMethod = -1;
        boolean validPayMethodInput = false;
        while (!validPayMethodInput) {
            System.out.print("Your choice: ");
            try {
                payMethod = scanner.nextInt();
                scanner.nextLine();
                validPayMethodInput = true;
            } catch (Exception e) {
                System.out.println("Please enter a valid number!");
                scanner.nextLine();
            }
        }
        PaymentSystem paymentSystem = new PaymentSystem();
        boolean paid = false;
        switch (payMethod) {
            case 1:
                paid = paymentSystem.processPayment(new CashPayment(), discountedTotal, currentStudent);
                break;
            case 2:
                paid = paymentSystem.processPayment(new CreditCardPayment(), discountedTotal, currentStudent);
                break;
            case 3:
                paid = paymentSystem.processPayment(new MobilePayment(), discountedTotal, currentStudent);
                break;
            case 4:
                paid = paymentSystem.processPayment(new VisaPayment(), discountedTotal, currentStudent);
                break;
            default:
                System.out.println("Invalid payment method.\n");
                break;
        }
        if (paid) {
            if (!payOrder.isPaid()) {
                payOrder.setPaid(true);

                if (orderProcessor instanceof OrderProcessor) {
                    ((OrderProcessor) orderProcessor).payOrder(payOrder.getOrderId());
                }
                reportManager.addToTotalSales(discountedTotal);
                try {
                    if (loyaltyProgram != null && currentStudent != null) {
                        loyaltyProgram.awardPoints(currentStudent, discountedTotal);
                    }
                } catch (Exception e) {
                    System.out.println("Error awarding loyalty points: " + e.getMessage());
                }
                System.out.println("==============================================");
                System.out.println("\n✔ Payment successful!\n");
                System.out.println("==============================================");
            } else {
                System.out.println("==============================================");
                System.out.println("Order is already paid. You cannot pay again.");
                System.out.println("==============================================");
            }
        } else {
            System.out.println("==============================================");
            System.out.println("\n✖ Payment failed.\n");
            System.out.println("==============================================");
        }
    }

    private static void redeemPoints(Scanner scanner, ILoyaltyProgram loyaltyProgram, IStudent currentStudent) {
        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║     REDEEM LOYALTY POINTS    ║");
        System.out.println("╚══════════════════════════════╝");
        System.out.println("Choose a reward to redeem:");
        System.out.println("1. Free Coffee      (10 points)");
        System.out.println("2. Free Ice Cream   (15 points)");
        System.out.println("3. Free Pizza       (40 points)");
        System.out.println("4. Free Waffle      (20 points)");
        System.out.println("5. Free Donates     (30 points)");
        System.out.print("Enter the number of your choice: ");
        int reward = scanner.nextInt();
        scanner.nextLine();
        IredeemPoints redeemStrategy;
        int requiredPoints;
        switch (reward) {
            case 1:
                redeemStrategy = new FreeCoffee();
                requiredPoints = 10;
                break;
            case 2:
                redeemStrategy = new FreeIceCream();
                requiredPoints = 15;
                break;
            case 3:
                redeemStrategy = new FreePizza();
                requiredPoints = 40;
                break;
            case 4:
                redeemStrategy = new FreeWaffel();
                requiredPoints = 20;
                break;
            case 5:
                redeemStrategy = new FreeDonates();
                requiredPoints = 30;
                break;
            default:
                System.out.println("Invalid option.");
                return;
        }
        int currentPoints = 0;
        try {
            currentPoints = loyaltyProgram.getLoyaltyPoints(currentStudent);
        } catch (Exception e) {
            System.out.println("Error reading loyalty points: " + e.getMessage());
        }
        System.out
                .println("You have " + currentPoints + " points. This reward requires " + requiredPoints + " points.");
        if (currentPoints < requiredPoints) {
            System.out.println("Not enough points to redeem this reward.\n");
            return;
        }
        loyaltyProgram.setRedeemStrategy(redeemStrategy);
        boolean redeemed = loyaltyProgram.redeemPoints(currentStudent);
        if (redeemed) {
            System.out.println("==============================================");

            System.out.println("\n✔ Points redeemed successfully!\n");
            System.out.println("==============================================");

        } else {
            System.out.println("==============================================");

            System.out.println("\n✖ Redemption failed.\n");
            System.out.println("==============================================");

        }
    }

    private static void viewNotifications(INotificationSystem notificationSystem) {
        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║        NOTIFICATIONS         ║");
        System.out.println("╚══════════════════════════════╝");
        System.out.println("\n==============================================");
        for (String note : notificationSystem.getNotifications()) {
            System.out.println("- " + note);
        }
        System.out.println("==============================================");

        // notificationSystem.clearNotifications();///
    }

    private static void showStudentOrders(IOrderProcessor orderProcessor, IStudent currentStudent) {

        if (orderProcessor instanceof com.example.cafe.order.OrderProcessor op) {
            op.reloadOrdersFromFile();
        }
        java.util.List<IOrder> orders = orderProcessor.getOrdersByStudent(currentStudent);
        if (orders.isEmpty()) {
            System.out.println("\n╔══════════════════════════════╗");
            System.out.println("║      You have no orders.     ║");
            System.out.println("╚══════════════════════════════╝\n");
            return;
        }
        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║         YOUR ORDERS          ║");
        System.out.println("╚══════════════════════════════╝");
        orders.forEach(order -> {
            System.out.println("------------------------------");
            System.out.printf("Order ID: %-5d | Status: %-18s\n", order.getOrderId(), order.getStatus());
            System.out.printf("Total Price: %,.2f EGP\n", order.getTotalPrice());
            System.out.println("Items:");
            java.util.Map<String, Long> itemCount = order.getOrderMenuItemList().getOrderMenuItems().stream()
                    .collect(java.util.stream.Collectors.groupingBy(
                            omi -> omi.getMenuItem().getName(), java.util.stream.Collectors.counting()));
            itemCount.forEach((name, count) -> {
                System.out.printf("- %-20s x %d\n", name, count);
            });
        });
        System.out.println("------------------------------\n");
    }

    // ============================================AdminMethods==========================================
    private static void addItemToMenu(Scanner scanner, IMenuManager menuManager) {
        System.out.print("Item name: ");
        String itemName = scanner.nextLine();
        System.out.print("Description: ");
        String desc = scanner.nextLine();
        System.out.print("Price: ");
        double price = scanner.nextDouble();
        scanner.nextLine();
        System.out.println("Choose a category from the following list:");
        String[] categories = { "PIZZA", "SANDWICHES", "SNACKS", "HOT_DRINKS", "COLD_DRINKS", "ICECREAM", "DESSERTS" };
        for (String c : categories) {
            System.out.println("- " + c);
        }
        System.out.print("Type the category name exactly as shown above: ");
        String cat = scanner.nextLine();
        try {
            MenuItemCategory category = MenuItemCategory.valueOf(cat.toUpperCase());
            menuManager.addMenuItem(new MenuItem(itemName, desc, price, category));
            System.out.println("Item added.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid category. Please make sure you type it exactly as shown.");
        }
    }

    private static void editItemInMenu(Scanner scanner, IMenuManager menuManager) {
        showMenu(menuManager);
        System.out.print("Item name to edit: ");
        String editName = scanner.nextLine();
        IMenuItem itemToEdit = menuManager.getMenuItemByName(editName);
        if (itemToEdit == null) {
            System.out.println("Item not found.");
            return;
        }
        System.out.println("New name: ");
        String newName = scanner.nextLine();
        System.out.print("New description: ");
        String newDesc = scanner.nextLine();
        System.out.print("New price: ");
        double newPrice = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("New category: ");
        String newCat = scanner.nextLine();
        try {
            MenuItemCategory newCategory = MenuItemCategory.valueOf(newCat.toUpperCase());
            menuManager.editMenuItem(editName,
                    new MenuItem(newName, newDesc, newPrice, newCategory));
            System.out.println("Item edited.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid category.");
        }
    }

    private static void removeItemFromMenu(Scanner scanner, IMenuManager menuManager) {
        showMenu(menuManager);
        System.out.print("Item name to remove: ");
        String removeName = scanner.nextLine();
        menuManager.removeMenuItem(removeName);
        System.out.println("Item removed.");
    }

    private static void showTotalSales(IReportManager reportManager) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose report type:");
        System.out.println("1. Daily sales");
        System.out.println("2. Weekly sales");
        System.out.print("Your choice: ");
        int type = scanner.nextInt();
        scanner.nextLine();
        java.time.LocalDate today = java.time.LocalDate.now();
        double sales;
        if (type == 1) {
            if (reportManager instanceof com.example.cafe.report.ReportManager rm) {
                sales = rm.getDailySales(today);
                System.out.println("\n╔════════════════════════════════════╗");
                System.out.println("║         DAILY SALES REPORT         ║");
                System.out.println("╠════════════════════════════════════╣");
                System.out.printf("║   Sales for %s: %,-12.2f EGP   ║\n", today, sales);
                System.out.println("╚════════════════════════════════════╝\n");
            }
        } else if (type == 2) {
            if (reportManager instanceof com.example.cafe.report.ReportManager rm) {
                sales = rm.getWeeklySales(today);
                java.time.LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);
                java.time.LocalDate endOfWeek = startOfWeek.plusDays(6);
                System.out.println("\n╔════════════════════════════════════╗");
                System.out.println("║         WEEKLY SALES REPORT        ║");
                System.out.println("╠════════════════════════════════════╣");
                System.out.printf("║   Sales for %s to %s: %,-12.2f EGP   ║\n", startOfWeek, endOfWeek, sales);
                System.out.println("╚════════════════════════════════════╝\n");
            }
        } else {
            double totalSales = reportManager.getTotalSales();
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║           SALES REPORT            ║");
            System.out.println("╠════════════════════════════════════╣");
            System.out.printf("║   Total Sales: %,-12.2f EGP      ║\n", totalSales);
            System.out.println("╚════════════════════════════════════╝\n");
        }
    }

    private static void showLoyaltyRedemptionsReport(IReportManager reportManager) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose report type:");
        System.out.println("1. Daily redemptions");
        System.out.println("2. Weekly redemptions");
        System.out.print("Your choice: ");
        int type = scanner.nextInt();
        scanner.nextLine();
        java.time.LocalDate today = java.time.LocalDate.now();
        if (type == 1) {
            if (reportManager instanceof com.example.cafe.report.ReportManager rm) {
                int dailyRedemptions = rm.getDailyRedemptions(today);
                int dailyRedeemedPoints = rm.getDailyRedeemedPoints(today);
                System.out.println("\n╔══════════════════════════════╗");
                System.out.println("║   DAILY LOYALTY REDEMPTIONS  ║");
                System.out.println("╚══════════════════════════════╝");
                System.out.println("Points redeemed for " + today + ": " + dailyRedeemedPoints);
                System.out.println("Redemptions for " + today + ": " + dailyRedemptions);
            }
        } else if (type == 2) {
            if (reportManager instanceof com.example.cafe.report.ReportManager rm) {
                int weeklyRedeemedPoints = rm.getWeeklyRedeemedPoints(today);
                int weeklyRedemptions = rm.getWeeklyRedemptions(today);
                java.time.LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);
                java.time.LocalDate endOfWeek = startOfWeek.plusDays(6);
                System.out.println("\n╔══════════════════════════════╗");
                System.out.println("║   WEEKLY LOYALTY REDEMPTIONS ║");
                System.out.println("╚══════════════════════════════╝");
                System.out.println(
                        "Points redeemed for week " + startOfWeek + " to " + endOfWeek + ": " + weeklyRedeemedPoints);
                System.out
                        .println("Redemptions for week " + startOfWeek + " to " + endOfWeek + ": " + weeklyRedemptions);
            }
        } else {
            int totalRedemptions = reportManager.getTotalRedemptions();
            int totalRedeemedPoints = reportManager.getTotalRedeemedPoints();
            System.out.println("\n╔══════════════════════════════╗");
            System.out.println("║   LOYALTY REDEMPTIONS        ║");
            System.out.println("╚══════════════════════════════╝");
            System.out.println("Total Redemptions: " + totalRedemptions);
            System.out.println("Total Redeemed Points: " + totalRedeemedPoints + "\n");
        }
    }

    private static void showPendingOrders(IStaff staff) {
        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║       PENDING ORDERS         ║");
        System.out.println("╚══════════════════════════════╝");
        staff.getPendingOrders().stream()
                .sorted(java.util.Comparator.comparingDouble(IOrder::getTotalPrice).reversed())
                .forEach(o -> System.out.printf("Order ID: %-5d | Price: %6.2f EGP | Status: %-18s\n", o.getOrderId(),
                        o.getTotalPrice(), o.getStatus()));
    }

    private static void markOrderAsPreparing(Scanner scanner, IStaff staff) {

        java.util.List<IOrder> pendingOrders = staff.getPendingOrders();
        if (pendingOrders.isEmpty()) {
            System.out.println("\nNo orders available!\n");
            return;
        }
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║              Current Orders List                   ║");
        System.out.println("╠════════════════════════════════════════════════════╣");
        System.out.println("║  Order ID  │      Status                           ║");
        System.out.println("╠════════════════════════════════════════════════════╣");
        for (IOrder o : pendingOrders) {
            System.out.printf("║   %-7d │ %-30s ║\n", o.getOrderId(), o.getStatus());
        }
        System.out.println("╚════════════════════════════════════════════════════╝\n");
        System.out.print("Enter Order ID to mark as preparing: ");
        int preparingOrderId = scanner.nextInt();
        scanner.nextLine();
        IOrder preparingOrder = staff.getOrderById(preparingOrderId);
        if (preparingOrder != null) {
            if (preparingOrder.isPaid()) {
                staff.markOrderPreparing(preparingOrderId);

                System.out.println("Order " + preparingOrderId + " marked as PREPARING.");
            } else {
                System.out.println("Order not paid yet !!");
            }
        } else {
            System.out.println("Order not found.");
        }
    }

    private static void markOrderAsReady(Scanner scanner, IStaff staff) {

        java.util.List<IOrder> preparingOrders = staff.getPreparingOrders();
        if (preparingOrders.isEmpty()) {
            System.out.println("\nNo orders available!\n");
            return;
        }
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║              Current Orders List                   ║");
        System.out.println("╠════════════════════════════════════════════════════╣");
        System.out.println("║  Order ID  │      Status                           ║");
        System.out.println("╠════════════════════════════════════════════════════╣");
        for (IOrder o : preparingOrders) {
            System.out.printf("║   %-7d │ %-30s ║\n", o.getOrderId(), o.getStatus());
        }
        System.out.println("╚════════════════════════════════════════════════════╝\n");
        System.out.print("Enter Order ID to mark as ready: ");
        int readyOrderId = scanner.nextInt();
        scanner.nextLine();
        IOrder readyOrder = staff.getOrderById(readyOrderId);
        if (readyOrder != null) {
            if (readyOrder.isPaid()) {
                if (readyOrder.getStatus() == com.example.cafe.order.OrderState.PREPARING) {
                    staff.markOrderReady(readyOrderId);
                    System.out.println("Order " + readyOrderId + " marked as READY FOR PICKUP.");
                } else {
                    System.out.println("Order must be in PREPARING status before marking as READY FOR PICKUP.");
                }
            } else {
                System.out.println("Order not paid yet !!");
            }
        } else {
            System.out.println("Order not found.");
        }
    }

    // ============================================ExitMethods==========================================
    private static boolean exitProgram() {
        System.out.println("Goodbye!");
        return false;
    }
}