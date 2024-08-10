public class CarController extends Controller {
    public CarController(Elevator e) {
        super(e);
    }

    public boolean isCertain(int position, int direction) {
        ElevatorWaitingQueue queue = getElevator().getElevatorWaitingQueue();
        synchronized (queue) {
            if (getSum() >= getArgument().getMaxCapacity() ||
                    queue.getIn(position).isEmpty()) {
                queue.notifyAll();
                return false;
            } else {
                for (Person person : queue.getIn(position)) {
                    if (person.getDirection() == direction) {   //如果指定方向
                        queue.notifyAll();
                        return true;
                    }
                }
                queue.notifyAll();
                return false;
            }
        }
    }
}
