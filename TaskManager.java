package taskManager;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.time.*;
import java.util.stream.Collectors;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.event.*;
import java.time.temporal.ChronoUnit;



class Task {
	String name;
	int day;
	String month;
	int year;
	String priority;
	
	//constructor
    public Task(String name, int day, String month, int year, String priority) {
        this.name = name;
        this.day = day;
        this.month = month;
        this.year = year;
        this.priority = priority;
    }
    
    //Getters
    public String getName() {
        return name;
    }
	public int getDay() {
        return day;
    }
    public String getMonth() {
        return month;
    }
    public int getYear() {
        return year;
    }
    public String getPriority() {
        return priority;
    }

    //checks for if tasks are due today, tomorrow, in the next 7 days, or overdue.
    public boolean isToday() {
    	int todayDay = LocalDate.now().getDayOfMonth();
		String todayMonth = LocalDate.now().getMonth().toString();
        int todayYear = LocalDate.now().getYear();
        return this.day == todayDay && this.month.equalsIgnoreCase(todayMonth) && this.year == todayYear;
    }
    public boolean isTomorrow() {
    	int tomorrowDay = LocalDate.now().plusDays(1).getDayOfMonth();
		String tomorrowMonth = LocalDate.now().getMonth().toString();
        int tomorrowYear = LocalDate.now().getYear();
        return this.day == tomorrowDay && this.month.equalsIgnoreCase(tomorrowMonth) && this.year == tomorrowYear;
    }
    public boolean isNext7Days() {
    	int monthValue = Month.valueOf(this.month.toUpperCase()).getValue();
        LocalDate taskDate = LocalDate.of(this.year, monthValue, this.day);
        LocalDate today = LocalDate.now();
        long daysBetween = ChronoUnit.DAYS.between(today, taskDate);
        return daysBetween > 0 && daysBetween <= 7;
    }
    public boolean isOverdue() {
        int monthValue = Month.valueOf(this.month.toUpperCase()).getValue();
        LocalDate taskDate = LocalDate.of(this.year, monthValue, this.day);
        LocalDate today = LocalDate.now();
        return taskDate.isBefore(today);
    }
}


public class TaskManager {
	static JScrollPane scrollPaneToday;
	static JScrollPane scrollPaneTomorrow;
	static JScrollPane scrollPaneNext7Days;
	static JScrollPane scrollPaneOverdue;
	static JScrollPane scrollPaneAllTasks;
	private JFrame mainMenu;
	
	
	public static JTable createTable(List<Task> tasks) {
		String[] columnNames = {"Name", "Day", "Month", "Year", "Priority"};
		Object[][] data = new Object[tasks.size()][5];
	    for (int i = 0; i < tasks.size(); i++) {
	        Task task = tasks.get(i);
	        data[i][0] = task.getName();
	        data[i][1] = task.getDay();
	        data[i][2] = task.getMonth();
	        data[i][3] = task.getYear();
	        data[i][4] = task.getPriority();
	    }
	    
	    //makes it so user cannot edit the table
	    DefaultTableModel model = new DefaultTableModel(data, columnNames) {
	        @Override
	        public boolean isCellEditable(int row, int column) {
	            return false;
	        }
	    };
	    
	    JTable table = new JTable(model);
	    
	    //modifies the table to be center aligned
	    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
	    centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
	    for (int i = 0; i < table.getColumnCount(); i++) {
	        table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
	    }
	    
	    return table;
	}
	
	public static void refreshTasksAndUI() {
		List<Task> tasks = readTasksFromFile();
	    List<Task> todayTasks = tasks.stream().filter(Task::isToday).collect(Collectors.toList());
	    List<Task> tomorrowTasks = tasks.stream().filter(Task::isTomorrow).collect(Collectors.toList());
	    List<Task> next7DaysTasks = tasks.stream().filter(Task::isNext7Days).collect(Collectors.toList());
	    List<Task> overdueTasks = tasks.stream().filter(Task::isOverdue).collect(Collectors.toList());

	    scrollPaneToday.setViewportView(createTable(todayTasks));
	    scrollPaneTomorrow.setViewportView(createTable(tomorrowTasks));
	    scrollPaneNext7Days.setViewportView(createTable(next7DaysTasks));
	    scrollPaneOverdue.setViewportView(createTable(overdueTasks));	    
	    scrollPaneAllTasks.setViewportView(createTable(tasks));
	}
	
	public static List<Task> readTasksFromFile() {
        List<Task> tasks = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader("tasks.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String name = line.split(", ")[0];
                int day = Integer.parseInt(line.split(", ")[1]);
                String month = line.split(", ")[2];
                int year = Integer.parseInt(line.split(", ")[3]);
                String priority = line.split(", ")[4];
                Task task = new Task(name, day, month, year, priority);
                tasks.add(task);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + "tasks.txt");
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + "tasks.txt");
        }
		return tasks;
	}
	
	//Launch the application.
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TaskManager windowMain = new TaskManager();
					windowMain.mainMenu.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	//Create the application.
	public TaskManager() {
		mainMenu = new JFrame();
		mainMenu.setBounds(100, 100, 1321, 683);
		mainMenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainMenu.getContentPane().setLayout(null);
        
		JButton btnAddTask = new JButton("+ Add Task");
		btnAddTask.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AddTaskMenu newTask = new AddTaskMenu();
				newTask.addTaskMenu.setVisible(true);
			}
		});
		btnAddTask.setBackground(Color.LIGHT_GRAY);
		btnAddTask.setFont(new Font("Arial", Font.BOLD, 28));
		btnAddTask.setBounds(70, 31, 556, 63);
		mainMenu.getContentPane().add(btnAddTask);
		
		JButton btnDeleteTask = new JButton("- Delete Task");
		btnDeleteTask.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DeleteTaskMenu deleteTask = new DeleteTaskMenu();
				deleteTask.deleteTaskMenu .setVisible(true);
			}
		});
		btnDeleteTask.setFont(new Font("Arial", Font.BOLD, 28));
		btnDeleteTask.setBackground(Color.LIGHT_GRAY);
		btnDeleteTask.setBounds(665, 31, 556, 63);
		mainMenu.getContentPane().add(btnDeleteTask);
		
		JLabel lblToday = new JLabel("Today");
		lblToday.setFont(new Font("Arial", Font.BOLD, 28));
		lblToday.setBounds(70, 98, 556, 45);
		mainMenu.getContentPane().add(lblToday);
		
		JLabel lblNextDays = new JLabel("Next 7 Days");
		lblNextDays.setFont(new Font("Arial", Font.BOLD, 28));
		lblNextDays.setBounds(70, 276, 556, 45);
		mainMenu.getContentPane().add(lblNextDays);
		
		JLabel lblTomorrow = new JLabel("Tomorrow");
		lblTomorrow.setFont(new Font("Arial", Font.BOLD, 28));
		lblTomorrow.setBounds(665, 98, 556, 45);
		mainMenu.getContentPane().add(lblTomorrow);
		
		JLabel lblOverdue = new JLabel("Overdue");
		lblOverdue.setFont(new Font("Arial", Font.BOLD, 28));
		lblOverdue.setBounds(70, 457, 556, 45);
		mainMenu.getContentPane().add(lblOverdue);
		
		JLabel lblAllTasks = new JLabel("All Tasks");
		lblAllTasks.setFont(new Font("Arial", Font.BOLD, 28));
		lblAllTasks.setBounds(665, 457, 556, 45);
		mainMenu.getContentPane().add(lblAllTasks);

        scrollPaneToday = new JScrollPane();
		scrollPaneToday.setBounds(70, 141, 556, 125);
		mainMenu.getContentPane().add(scrollPaneToday);
		
		scrollPaneTomorrow = new JScrollPane();
		scrollPaneTomorrow.setBounds(666, 141, 556, 125);
		mainMenu.getContentPane().add(scrollPaneTomorrow);

		scrollPaneNext7Days = new JScrollPane();
		scrollPaneNext7Days.setBounds(70, 319, 1152, 125);
		mainMenu.getContentPane().add(scrollPaneNext7Days);
		
		scrollPaneOverdue = new JScrollPane();
		scrollPaneOverdue.setBounds(70, 500, 556, 125);
		mainMenu.getContentPane().add(scrollPaneOverdue);
		
		scrollPaneAllTasks = new JScrollPane();
		scrollPaneAllTasks.setBounds(665, 500, 556, 125);
		mainMenu.getContentPane().add(scrollPaneAllTasks);
		
		refreshTasksAndUI();
	}
}

//Add task menu.
class AddTaskMenu {
	JFrame addTaskMenu;

	//Method for updating days options based on changes in month or year selections.
	private void updateDays(JComboBox<Integer> dayBox, int monthIndex, int year) {
        int daysInMonth; 
		if (monthIndex == 1) {
            if (year % 4 == 0) {
            	daysInMonth = 29;
            } else {
            	daysInMonth = 28;
            }
        } else if (monthIndex == 3 || monthIndex == 5 || monthIndex == 8 || monthIndex == 10) {
        	daysInMonth = 30;
        } else {
        	daysInMonth = 31;
        }
		dayBox.removeAllItems();
        for (int day = 1; day <= daysInMonth; day++) {
            dayBox.addItem(day);
        }
    }
	
	//Launch the second frame.
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AddTaskMenu windowAdd = new AddTaskMenu();
					windowAdd.addTaskMenu.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	//Create the second frame.
	public AddTaskMenu() {
		addTaskMenu = new JFrame();
		addTaskMenu.setBounds(100, 100, 651, 365);
		addTaskMenu.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addTaskMenu.getContentPane().setLayout(null);
		
		addTaskMenu.setResizable(false);
		
		JLabel lblErrorMessage = new JLabel("");
		lblErrorMessage.setFont(new Font("Arial", Font.PLAIN, 16));
        lblErrorMessage.setForeground(Color.RED);
        lblErrorMessage.setBounds(10, 238, 461, 26);
        lblErrorMessage.setVisible(false);
        addTaskMenu.getContentPane().add(lblErrorMessage);
		
		JLabel lblNewTask = new JLabel("New Task");
		lblNewTask.setFont(new Font("Arial", Font.BOLD, 42));
		lblNewTask.setBounds(10, 10, 324, 71);
		addTaskMenu.getContentPane().add(lblNewTask);
		
		JLabel lblName = new JLabel("Name");
		lblName.setFont(new Font("Arial", Font.PLAIN, 16));
		lblName.setBounds(10, 73, 324, 26);
		addTaskMenu.getContentPane().add(lblName);
		
		JLabel lblDay = new JLabel("Day");
		lblDay.setFont(new Font("Arial", Font.PLAIN, 16));
		lblDay.setBounds(10, 127, 48, 26);
		addTaskMenu.getContentPane().add(lblDay);
		
		JLabel lblMonth = new JLabel("Month");
		lblMonth.setFont(new Font("Arial", Font.PLAIN, 16));
		lblMonth.setBounds(105, 127, 145, 26);
		addTaskMenu.getContentPane().add(lblMonth);
		
		JLabel lblYear = new JLabel("Year");
		lblYear.setFont(new Font("Arial", Font.PLAIN, 16));
		lblYear.setBounds(326, 127, 145, 26);
		addTaskMenu.getContentPane().add(lblYear);
		
		JLabel lblPriority = new JLabel("Priority");
		lblPriority.setFont(new Font("Arial", Font.PLAIN, 16));
		lblPriority.setBounds(10, 182, 324, 26);
		addTaskMenu.getContentPane().add(lblPriority);
		
		JTextField nameField = new JTextField();
		nameField.setFont(new Font("Arial", Font.PLAIN, 16));
		nameField.setBounds(10, 98, 461, 26);
		addTaskMenu.getContentPane().add(nameField);
		nameField.setColumns(10);
		
		int[] days = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31};
		String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
		int[] years = {2024, 2025, 2026, 2027, 2028};

		JComboBox<String> monthBox = new JComboBox<>();
		for (String month : months) {
            monthBox.addItem(month);
        }
		monthBox.setBounds(105, 152, 145, 25);
		monthBox.setSelectedIndex(0);
		addTaskMenu.getContentPane().add(monthBox);
				
		JComboBox<Integer> dayBox = new JComboBox<>();
		for (int day : days) {
			dayBox.addItem(day);
		}		
		dayBox.setBounds(10, 152, 48, 25);
		dayBox.setSelectedIndex(0);
		addTaskMenu.getContentPane().add(dayBox);
		
		JComboBox<Integer> yearBox = new JComboBox<>();
		for (int year : years) {
            yearBox.addItem(year);
		}
		yearBox.setBounds(326, 152, 145, 25);
		yearBox.setSelectedIndex(0);
		addTaskMenu.getContentPane().add(yearBox);
		
		monthBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dayBox.removeAllItems();
				int selectedMonthIndex = monthBox.getSelectedIndex();
                int selectedYear = (int) yearBox.getSelectedItem();
                updateDays(dayBox, selectedMonthIndex, selectedYear);
			}
		});
		
		yearBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedMonthIndex = monthBox.getSelectedIndex();
                int selectedYear = (int) yearBox.getSelectedItem();
                updateDays(dayBox, selectedMonthIndex, selectedYear);
			}
		});
		
		JComboBox<String> priorityBox = new JComboBox<String>();
		priorityBox.setMaximumRowCount(3);
		priorityBox.setFont(new Font("Arial", Font.PLAIN, 16));
		priorityBox.setBounds(10, 208, 461, 26);
		addTaskMenu.getContentPane().add(priorityBox);
		priorityBox.addItem("High");
		priorityBox.addItem("Medium");
		priorityBox.addItem("Low");
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String name = nameField.getText();
				int day = (int) dayBox.getSelectedItem();
				String month = (String) monthBox.getSelectedItem();
				int year = (int) yearBox.getSelectedItem();
				String priority = (String) priorityBox.getSelectedItem();
		        boolean success = true;
				try (BufferedWriter writer = new BufferedWriter(new FileWriter("tasks.txt", true))) {
		            writer.write(name + ", " + day + ", " + month + ", " + year + ", " + priority);
		            writer.newLine();
		        } catch (IOException e1) {
					lblErrorMessage.setText("Error writing to the file. Ensure file is accessible.");
                    lblErrorMessage.setVisible(true);
		            success = false;
		        }
				if (success == true) {
					TaskManager.refreshTasksAndUI();
					addTaskMenu.dispose();
				}
				
				
			}
		});
		btnSave.setFont(new Font("Arial", Font.PLAIN, 16));
		btnSave.setBounds(10, 268, 145, 35);
		addTaskMenu.getContentPane().add(btnSave);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addTaskMenu.dispose();
			}
		});
		btnCancel.setFont(new Font("Arial", Font.PLAIN, 16));
		btnCancel.setBounds(176, 268, 145, 35);
		addTaskMenu.getContentPane().add(btnCancel);
		
	}
}

//Delete task menu.
class DeleteTaskMenu {
	JFrame deleteTaskMenu;
	
	//Launch the application.
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DeleteTaskMenu windowDelete = new DeleteTaskMenu();
					windowDelete.deleteTaskMenu.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	//Create the application.
	public DeleteTaskMenu() {
		deleteTaskMenu = new JFrame();
		deleteTaskMenu.setBounds(100, 100, 651, 365);
		deleteTaskMenu.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		deleteTaskMenu.getContentPane().setLayout(null);
		
		deleteTaskMenu.setResizable(false);
		
		JLabel lblErrorMessage = new JLabel("");
		lblErrorMessage.setFont(new Font("Arial", Font.PLAIN, 16));
        lblErrorMessage.setForeground(Color.RED);
        lblErrorMessage.setBounds(10, 235, 461, 26);
        lblErrorMessage.setVisible(false);
        deleteTaskMenu.getContentPane().add(lblErrorMessage);
		
		JLabel lblDeleteTask = new JLabel("Delete Task");
		lblDeleteTask.setFont(new Font("Arial", Font.BOLD, 42));
		lblDeleteTask.setBounds(10, 10, 324, 71);
		deleteTaskMenu.getContentPane().add(lblDeleteTask);
		
		JLabel lblTaskName = new JLabel("Task Name");
		lblTaskName.setFont(new Font("Arial", Font.PLAIN, 16));
		lblTaskName.setBounds(10, 73, 324, 26);
		deleteTaskMenu.getContentPane().add(lblTaskName);
				
		List<Task> tasks = TaskManager.readTasksFromFile();
		JComboBox<String> taskBox = new JComboBox<String>();
		taskBox.setMaximumRowCount(tasks.size());
		taskBox.setFont(new Font("Arial", Font.PLAIN, 16));
		taskBox.setBounds(10, 98, 461, 26);
		deleteTaskMenu.getContentPane().add(taskBox);
		
		for (int i = 0; i < tasks.size(); i++) {
			taskBox.addItem(tasks.get(i).getName());
		}
		
		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String target = ((String) taskBox.getSelectedItem());
				File ogFile = new File("tasks.txt");
		        File tempFile = new File(ogFile.getAbsolutePath() + ".tmp");
		        lblErrorMessage.setVisible(false);
		        boolean success = true;
		        
		        try (BufferedReader reader = new BufferedReader(new FileReader(ogFile)); BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
		            String currentLine;
		            while ((currentLine = reader.readLine()) != null) {
		                if (currentLine.contains(target) == false) {
		                    writer.write(currentLine + System.lineSeparator());
		                }
		            }
		            
		        } catch (FileNotFoundException e1) {
		        	lblErrorMessage.setText("File not found.");
		        	lblErrorMessage.setVisible(true);
		        	success = false;
				} catch (IOException e1) {
					lblErrorMessage.setText("Error accessing the task list. Ensure the file is not open.");
                    lblErrorMessage.setVisible(true);
                    success = false;
				}
		        if (success) {
			        // Delete the original file
		        	if (!ogFile.delete()) {
		        		lblErrorMessage.setText("Error accessing the task list. Ensure the file is not open.");
		                lblErrorMessage.setVisible(true);
		                success = false;
			        }
			        
			        // Rename the temporary file to the original file
			        if (!tempFile.renameTo(ogFile)) {
			        	lblErrorMessage.setText("Could not update the task list.");
		                lblErrorMessage.setVisible(true);
		                success = false;
			        }
		        }
		        if (success == true) {
		        	TaskManager.refreshTasksAndUI();
		        	deleteTaskMenu.dispose();
		        }
			}
		});
		btnDelete.setFont(new Font("Arial", Font.PLAIN, 16));
		btnDelete.setBounds(10, 268, 145, 35);
		deleteTaskMenu.getContentPane().add(btnDelete);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteTaskMenu.dispose();
			}
		});
		btnCancel.setFont(new Font("Arial", Font.PLAIN, 16));
		btnCancel.setBounds(176, 268, 145, 35);
		deleteTaskMenu.getContentPane().add(btnCancel);
		
	}
}
