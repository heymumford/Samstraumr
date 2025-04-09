Feature: Task Notification Integration Tests
  As a system operator
  I want tasks to trigger notifications at appropriate times
  So that users can be informed of task status and completion

  Background:
    * def ComponentUtils = read('classpath:karate/helpers/component-utils.js')
    * def taskExecutor = Java.type('org.s8r.test.mock.MockTaskExecutionAdapter').createInstance()
    * def notificationAdapter = Java.type('org.s8r.test.mock.MockNotificationAdapter').createInstance()
    * def taskNotificationBridge = Java.type('org.s8r.infrastructure.integration.TaskNotificationBridge').create(taskExecutor, notificationAdapter)
    * def taskId = 'task-' + Java.type('java.util.UUID').randomUUID()
    * def recipient = 'user@example.com'

  Scenario: Task completion should trigger a notification
    Given taskExecutor.configure({ simulateDelay: false })
    And notificationAdapter.configure({ deliveryMethod: 'EMAIL' })
    When def task = taskExecutor.scheduleTask(taskId, 'Test Task', 'This is a test task', 0)
    And taskNotificationBridge.registerTaskCompletionNotification(taskId, recipient, 'Your task has completed')
    And task.status = 'COMPLETED'
    And taskExecutor.updateTask(task)
    Then def notifications = notificationAdapter.getSentNotifications()
    And match notifications.length == 1
    And match notifications[0].recipient == recipient
    And match notifications[0].subject contains 'completed'
    And match notifications[0].deliveryStatus == 'DELIVERED'

  Scenario: Scheduled task with defined completion time should trigger a notification
    Given taskExecutor.configure({ simulateDelay: true, delayMilliseconds: 500 })
    And notificationAdapter.configure({ deliveryMethod: 'EMAIL' })
    When def futureTime = Java.type('java.time.Instant').now().plusSeconds(1).toString()
    And def task = taskExecutor.scheduleTask(taskId, 'Scheduled Task', 'This task is scheduled', futureTime)
    And taskNotificationBridge.registerTaskCompletionNotification(taskId, recipient, 'Scheduled task completed')
    And eval
      """
      var Thread = Java.type('java.lang.Thread');
      Thread.sleep(1500); // Wait for the task to complete
      """
    Then def notifications = notificationAdapter.getSentNotifications()
    And match notifications.length == 1
    And match notifications[0].recipient == recipient
    And match notifications[0].subject contains 'Scheduled task completed'
    And match notifications[0].deliveryStatus == 'DELIVERED'

  Scenario: Task failure should trigger an error notification
    Given taskExecutor.configure({ simulateErrors: true, errorRate: 1.0 })
    And notificationAdapter.configure({ deliveryMethod: 'EMAIL' })
    When def task = taskExecutor.scheduleTask(taskId, 'Failing Task', 'This task will fail', 0)
    And taskNotificationBridge.registerTaskErrorNotification(taskId, recipient, 'Task has failed')
    And eval
      """
      var Thread = Java.type('java.lang.Thread');
      Thread.sleep(500); // Wait for the task to fail
      """
    Then def notifications = notificationAdapter.getSentNotifications()
    And match notifications.length == 1
    And match notifications[0].recipient == recipient
    And match notifications[0].subject contains 'failed'
    And match notifications[0].body contains task.errorDetails

  Scenario: Multiple notifications should be sent for recurring tasks
    Given taskExecutor.configure({ simulateDelay: true, delayMilliseconds: 200 })
    And notificationAdapter.configure({ deliveryMethod: 'EMAIL' })
    When def recurring = { interval: 'PT0.5S', count: 3 }
    And def task = taskExecutor.scheduleRecurringTask(taskId, 'Recurring Task', 'This task runs multiple times', recurring)
    And taskNotificationBridge.registerRecurringTaskNotification(taskId, recipient, 'Recurring task ran - #{iterationCount}')
    And eval
      """
      var Thread = Java.type('java.lang.Thread');
      Thread.sleep(2000); // Wait for all iterations to complete
      """
    Then def notifications = notificationAdapter.getSentNotifications()
    And assert notifications.length >= 3
    And match notifications[0].subject contains 'Recurring task ran - 1'
    And match notifications[1].subject contains 'Recurring task ran - 2'
    And match notifications[2].subject contains 'Recurring task ran - 3'

  Scenario: Task notifications should respect delivery preferences
    Given taskExecutor.configure({ simulateDelay: false })
    And notificationAdapter.configure({ deliveryMethod: 'SMS' })
    When def task = taskExecutor.scheduleTask(taskId, 'Test Task', 'This is a test task', 0)
    And taskNotificationBridge.registerTaskCompletionNotification(taskId, recipient, 'Your task has completed', { deliveryMethod: 'SMS' })
    And task.status = 'COMPLETED'
    And taskExecutor.updateTask(task)
    Then def notifications = notificationAdapter.getSentNotifications()
    And match notifications.length == 1
    And match notifications[0].recipient == recipient
    And match notifications[0].deliveryMethod == 'SMS'

  Scenario: Task notification with retry should eventually succeed
    Given taskExecutor.configure({ simulateDelay: false })
    And notificationAdapter.configure({ simulateDeliveryFailures: true, failureCount: 2, eventuallySucceed: true })
    When def task = taskExecutor.scheduleTask(taskId, 'Test Task', 'This is a test task', 0)
    And taskNotificationBridge.registerTaskCompletionNotification(taskId, recipient, 'Your task has completed', { retry: true, maxRetries: 3 })
    And task.status = 'COMPLETED'
    And taskExecutor.updateTask(task)
    Then def notifications = notificationAdapter.getSentNotifications()
    And assert notifications.length >= 3
    And match notifications[0].deliveryStatus == 'FAILED'
    And match notifications[1].deliveryStatus == 'FAILED'
    And match notifications[2].deliveryStatus == 'DELIVERED'

  Scenario: Task progress updates should trigger notifications at specified thresholds
    Given taskExecutor.configure({ simulateProgress: true })
    And notificationAdapter.configure({ deliveryMethod: 'EMAIL' })
    When def task = taskExecutor.scheduleTask(taskId, 'Progress Task', 'This task reports progress', 0)
    And taskNotificationBridge.registerProgressNotifications(taskId, recipient, 'Task progress: #{progressPercent}%', [25, 50, 75, 100])
    And eval
      """
      var Thread = Java.type('java.lang.Thread');
      // Simulate progress updates
      for (var i = 0; i <= 100; i += 5) {
        taskExecutor.updateTaskProgress(taskId, i);
        Thread.sleep(100);
      }
      """
    Then def notifications = notificationAdapter.getSentNotifications()
    And assert notifications.length == 4
    And match notifications[0].body contains 'Task progress: 25%'
    And match notifications[1].body contains 'Task progress: 50%'
    And match notifications[2].body contains 'Task progress: 75%'
    And match notifications[3].body contains 'Task progress: 100%'