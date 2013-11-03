package ru.fizteh.fivt.students.chernigovsky.filemap;

public class StateProvider {
    private State currentState;

    public void changeCurrentState(State newState) {
        currentState = newState;
    }

    public State getCurrentState() {
        return currentState;
    }

}
