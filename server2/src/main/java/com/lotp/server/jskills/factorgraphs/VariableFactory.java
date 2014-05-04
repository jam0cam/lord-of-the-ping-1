package com.lotp.server.jskills.factorgraphs;

public class VariableFactory<TValue> {

    // using a Func<TValue> to encourage fresh copies in case it's overwritten
    protected final Func<TValue> variablePriorInitializer;

    public VariableFactory(Func<TValue> variablePriorInitializer) {
        this.variablePriorInitializer = variablePriorInitializer;
    }

    public Variable<TValue> createBasicVariable(String nameFormat, Object... args) {
        return new Variable<TValue>(variablePriorInitializer.eval(), String.format(nameFormat, args));
    }
}