SmarTodo
========

An Android todo app, though a massively smart one

Named and sharable lists of ToDo tasks that are location and time sensitive.

## Main Activity
Shows existing ToDo task lists (e.g., Lake Tahoe Trip, Grocery Shopping, Labor Day picnic, Apartment hunting, Product Launch,...).

1. User can create a new named ToDo task list.
1. User can view an existing ToDo task list.
1. User can delete a ToDo task list.
1. User can activate/deactivate a ToDo task list.
1. User can share a ToDo task list.

## ToDoTask Activity
1. Shows a particular ToDo task list.
1. User can add a new task. (detail view)
1. User can delete an existing task.
1. User can view the details of task. (detail view)
1. User can modify the details of task. (detail view) - add comment, mark completed, reassign, etc.

## ToDoTaskDetail Activity (Detail View)
Shows a particular ToDo task in detail  (task name, location name or tag, appropriate time to do the task, list of people doing the task, expiry date, associated image, etc.).

1. User can modify the ToDo task.
1. User can share the ToDo task.

## Task Views (Queries)

1. A: Show all the (outstanding) tasks 
1. B: Show all the tasks related to a particular ToDo task list.
1. C: Show all the tasks assigned to a particular user.
1. A, B, and C filtered by time (this evening, today, this week, this month...) and location.
1. Show all the tasks done in the last week (month, 6 months,...).

## Notifications
Time and Location based notifications for active ToDo task lists.
Notifications to all the assignees of a task when the task is completed. 

## Initial version
Creating a task list (e.g., shopping list, visiting clients list, apartments to see list, sightseeing list during a trip, etc.) such that it can tell us what can be done from our list when we are close to certain place (e.g. a grocery store, an apartment, an attraction, etc.).  A task list can be activated or deactivated or may be assigned an operating time (e.g., sightseeing task list may be activated when the trip actually starts). Essentially, it would be a time constrained and location constrained ToDo list which can remind us what task can be done when we are close to a place at a certain time. As and when a task is completed it can be checked off and recorded with some optional comments for future reference.

# Pitch
## Let’s Do It 
*It’s no longer about a list, it’s about getting sh!t done.*

Struggling to keep your life organized? 
The key to organization is having a simple system that works for you and your family. 

Imagine an app that reminds you to ...
* buy milk on your way home from the office 
* ask your spouse to pick up your child after school because it detects you have a conflicting meeting 
* book a vacation with loved ones 7 weeks before the long weekend when the airfare prices are lowest 
* call the insurance company when you have a free 15 min slot in your calendar during work hours

Core Concepts:

1. **Focus on productivity.** Make a list. Let the app organize it in the most efficient way
1. **Remind me right.** Configure smart Reminders - based on location / time / free-busy / day of week
1. **More the merrier.** Share an item or entire To Do list with your friends or family, so you can accomplish more together.. 
1. **Creating a list should be simple and easy.** Use voice, pictures or other media to easily create ToDos.
1. **Keep talking.** Have real time conversations around tasks and collaborate better
1. **Keep them together.** Integration of ToDos with Mail and Calendar, so you can manage them together and efficiently. 

Don’t miss out! Based on special events in your calendar or general events (like long weekends, back to school, black friday, christmas shopping etc.), relevant ToDos are suggested.
