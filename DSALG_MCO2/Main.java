import java.io.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.io.IOException;


public class Main {

    /**
     * Social graph represented by an adjacency list
     * Each key represents an account in the social network
     * The corresponding value of (List<Integer>) is a list of friends of that node
     */
    private static Map<Integer, List<Integer>> socialGraph = new HashMap<>();

    /**
     * Loads the social network data from the given file into the socialGraph
     * @param file the name of the file containing the social network data
     * @throws IOException If the program encounters an error while reading the file
     */
    private static void loadData(String file) throws IOException {
        System.out.println("\nLoading data...");

        //reads the first line of the file to get the number of accounts(n) and friendships(e)
        try (BufferedReader bufferedReader = new BufferedReader (new FileReader(file))) {
            String[] data = bufferedReader.readLine().trim().split("\\s+");
            int n = Integer.valueOf(data[0]); //number of accounts or the edges
            int e = Integer.valueOf(data[1]); //number of friendships or the vertices

            //initialize adjacency list for each node
            for(int i = 0; i < n; i++) {
                socialGraph.put(i, new ArrayList<>());
            }

            //populate the friendship data from the file
            for(int i = 0; i < e; i++) {
                String[] friends = bufferedReader.readLine().trim().split("\\s+");
                int x = Integer.valueOf(friends[0]);
                int y = Integer.valueOf(friends[1]);

                //adding friendship (bi-directional) to the adjacency of the nodes
                socialGraph.get(x).add(y);
                socialGraph.get(y).add(x);
            }
        }
        System.out.println("\nFile successfully loaded!");
    }

    /**
     * Displays the friend list from the given ID
     * @param file the name of the file containing the friendship data
     * @throws IOException If the program encounters an error while reading the file
     */
    private static void displayFriendList(String file) throws IOException {

        Scanner sc = new Scanner(System.in);

        //prompts the user to enter the ID number for which they want to see the friend list
        System.out.print("Enter ID Number: ");
        int ID = sc.nextInt();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            ArrayList<Integer> friends = new ArrayList<>();
            String line;

            //populate the list of friends given the ID by reading the friendship data from the file
            while((line = bufferedReader.readLine()) != null) {
                String[] friendship = line.trim().split("\\s+");
                int id1 = Integer.valueOf(friendship[0]);
                int id2 = Integer.valueOf(friendship[1]);

                //checks if the current friendship contains the user with the given ID
                //If yes, the friend's ID is added to the list of friends
                if(id1 == ID) {
                    friends.add(id2);
                } else if(id2 == ID) {
                    friends.add(id1);
                }
            }

            //checks if the ID is in the social graph
            if(!socialGraph.containsKey(ID)) {
                System.out.println("\n============================================");
                System.out.println("\nID does not exist.. Returning to Main Menu..");
                System.out.println("\n============================================");

            } else {
                //displays the friend list for the given ID
                System.out.println("\n===========================================");
                System.out.println("             Number of Friends           ");
                System.out.println("\nUser " + ID + " has " + friends.size() + " friend/s");
                System.out.println("\n===========================================");
                System.out.println("               Friend List                 ");
                System.out.println("\nUser " + ID + "'s Friend List:");
                for(int i = 0; i< friends.size(); i ++) {
                    int friend = friends.get(i);
                    System.out.println(friend);
                }
                System.out.println("\n===========================================");
                System.out.println("   Friend list successfully displayed!");
            }
        }
    }

    /**
     * Displays connection between two accounts in the social network
     * @param file the name of the file containing the friendship data
     * @throws IOException If the program encounters an error while reading the file
     */
    private static void displayConnection(String file) throws IOException {
        Scanner sc = new Scanner(System.in);

        //Prompts the user to input IDs of two accounts to find their connection
        System.out.println("Enter ID of first person: ");
        int personA = sc.nextInt();

        System.out.println("Enter ID of second person: ");
        int personB = sc.nextInt();


        //checks if both IDs inputted are in the social graph
        List<Integer> connection = new ArrayList<>();
        if (!socialGraph.containsKey(personA) || !socialGraph.containsKey(personB)) {
            System.out.println("\n============================================================");
            System.out.println("\nOne or both of the inputted IDs do not exist in the dataset.");
            System.out.println("\n============================================================");
            return;
        }

        // Checks if both inputted IDs are the same, meaning a connecting with oneself
        if (personA == personB) {
            System.out.println("\n===========================================");
            System.out.println("\n     The two IDs inputted are the same.");
            System.out.println("\n===========================================");
            return;
        }

        // Create an array to mark visited nodes during Depth-first search (DFS)
        boolean[] visited = new boolean[socialGraph.size()];

        // Performs the DFS to find a connection between the accounts
        dfs(personA, personB, visited, connection);

        // Checks if there is a connection between two accounts
        if (connection.isEmpty()) {
            System.out.println("Cannot find a connection between " + personA + " and " + personB);
        } else {
            // Displays the connection of the two accounts
            System.out.println("\n===========================================");
            System.out.println("             Friend Connection                 ");
            System.out.println("\nThere is a connection from " + personA + " to " + personB + "!");
            for (int i = 0; i < connection.size() - 1; i++) {
                int friend = connection.get(i);
                int nextFriend = connection.get(i + 1);
                System.out.println("\n" + friend + " is friends with " + nextFriend);
            }
            System.out.println("\n===========================================");
            System.out.println(" Friend connection successfully displayed!");
        }
    }

    /**
     * Depth-First Search (DFS) to fine a connection between two accounts
     * @param current The ID of the current person being checked in the DFs
     * @param target The ID of the person we are trying to find a connection to
     * @param visited An array that marks whether a person has been visited during DFS
     * @param connection A list that stores the current path of the friends being checked in the DFS
     * @return True if a connection is found, false otherwise
     */
    private static boolean dfs(int current, int target, boolean[] visited, List<Integer> connection) {
        visited[current] = true;
        connection.add(current);

        // Checks if the target person is found in the DFS path
        if (current == target) {
            return true;
        }

        // Traverse the friends of the current person
        for (int friend : socialGraph.get(current)) {
            //if the friend has not been visited yet, continue to search
            if (!visited[friend]) {
                if (dfs(friend, target, visited, connection)) {
                    return true; // return true if a connection is found
                }
            }
        }

        // if the target is not found in this branch, backtrack by removing the current person from the path
        connection.remove(connection.size() - 1);
        return false; // return false if no connection is found in the branch
    }

    /**
     * To run the social network graph program
     * @param args Command-line arguments
     */
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // asks the user to enter the file path containing the social network data
        System.out.println("Please enter the file path: ");
        String file = sc.nextLine();

        try {

            // load the social network data from the given file path into the socialGraph
            loadData(file);

            // main menu loop, allowing the user to choose different options
            while (true) {
                System.out.println("\nMAIN MENU");
                System.out.println("1. Display friend list");
                System.out.println("2. Display connections");
                System.out.println("3. Exit");


                System.out.println("\nPlease select an option: ");
                // checks if the user input is an integer input or not
                if (sc.hasNextInt()) {
                    int choice = sc.nextInt();

                    // process the user's choice based on selected option
                    switch (choice) {
                        case 1:
                            displayFriendList(file);
                            break;
                        case 2:
                            displayConnection(file);
                            break;
                        case 3:
                            System.out.println("\nExiting the program...");
                            sc.close();
                            return;
                        default:
                            System.out.println("\nInvalid input. Please try again.");
                            break;
                    }
                } else {
                    sc.next(); // if the user's input is not an integer, skip the current input to avoid error
                }
            }
        }catch (IOException e) {
            System.out.println("Failed to open and read the file!");
        }
    }
}
