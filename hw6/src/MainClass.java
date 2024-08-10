import com.oocourse.elevator2.TimableOutput;

import java.util.ArrayList;

public class MainClass {

    public static void main(String[] args) {
        TimableOutput.initStartTimestamp();
        SchedulerWaitingQueue schedulerWaitingQueue = new SchedulerWaitingQueue();
        ArrayList<ElevatorWaitingQueue> elevatorWaitingQueues = new ArrayList<>();
        Scheduler scheduler = new Scheduler(schedulerWaitingQueue, elevatorWaitingQueues);
        InputThread inputThread = new InputThread(schedulerWaitingQueue,elevatorWaitingQueues);

        inputThread.start();
        scheduler.start();

        for (int i = 1; i <= 6; i++) {
            ElevatorWaitingQueue elevatorWaitingQueue = new ElevatorWaitingQueue();
            elevatorWaitingQueues.add(elevatorWaitingQueue);
            Elevator elevator = new Elevator(i,scheduler,elevatorWaitingQueue);
            elevatorWaitingQueue.setElevator(elevator);
            elevator.start();
        }

    }
}
