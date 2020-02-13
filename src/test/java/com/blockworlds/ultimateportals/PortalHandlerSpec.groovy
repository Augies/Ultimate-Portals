package com.blockworlds.ultimateportals

import org.bukkit.block.BlockFace; //It's good practice to make one test class per java class, and mirror the package path of it.

import spock.lang.Specification;

/**
 * This class is in groovy, a language which runs in a JVM but is built with more of a focus on test-driven development
 * Its key differences are the lack of semicolons, the lack of "public", automatic getters/setters, and the ability to declare variables using "def" instead of a class name
 * It's super powerful for development but at the same time it adds too much overhead to be used for minecraft plugin development :(
 */
class PortalHandlerSpec extends Specification {//Specification is extended to tell the text editor that this is a test class

    /**
     * This example test is used to test the "getLogicDirection" function.
     * Since it's a static util function, it's quite easy to test
     * When testing objects, it won't be quite as easy :(
     * Good practice is to usually mock everything except the object you're testing
     * Often times, your code needs to be broken into smaller chunks in order to make it more easily testable
     * It's annoying at first but is worthwhile, as you can make sure everything's still working with the click of a button.
     */
    void "check getLogicDirection"(){
        expect:
        PortalHandler.getLogicDirection(blockface) == result

        //Tables can be setup in order to test multiple cases at once. We only need one test for all regular cases
        where:
        blockface            | result
        BlockFace.NORTH      | BlockFace.EAST
        BlockFace.SOUTH      | BlockFace.WEST
        BlockFace.EAST       | BlockFace.SOUTH
        BlockFace.WEST       | BlockFace.NORTH
    }

    /**
     * The previous test did not cover every case though. What if a "bad value" was thrown into the function?
     * In that case, we need to give the function a bad value and ensure that an exception was thrown
     * to do that we do this:
     */
    void "test bad cases for getLogicDirection"(){
        when:
        PortalHandler.getLogicDirection(BlockFace.NORTH_EAST)

        then: "An exception is thrown"
        thrown(IllegalStateException) //thrown is used to check if an exception is thrown. If one isn't, the test will fail
    }
}
