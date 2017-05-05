package com.fossgalaxy.object;

import com.fossgalaxy.object.annotations.ObjectDef;
import com.fossgalaxy.object.annotations.ObjectDefStatic;
import com.fossgalaxy.object.annotations.Parameter;

/**
 * Created by piers on 05/05/17.
 */
public class TestObject {

    private final String name;
    private UnkownObject unkownObject;

    @ObjectDef("Object")
    public TestObject(String name) {
        this.name = name;
    }

    public TestObject(String name, UnkownObject unkownObject){
        this.name = name;
        this.unkownObject = unkownObject;
    }


    @Override
    public String toString() {
        return "TestObject{" +
                "name='" + name + '\'' +
                '}';
    }

    @ObjectDefStatic("Name")
    public static TestObject makeObject(){
        return new TestObject("Name");
    }

    @ObjectDefStatic("WithUnknown")
    @Parameter(id=1, func="makeUnkownObject")
    public static TestObject makeObject(String name, UnkownObject unkownObject){
        return new TestObject(name, unkownObject);
    }

    public static UnkownObject makeUnkownObject(String info){
        return new UnkownObject(info);
    }
}
