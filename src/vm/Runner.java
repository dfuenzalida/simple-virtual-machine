package vm;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Runner {
    static Map<String, Integer> byteByName = new HashMap<>();
    static Map<String, Integer> numArgsByName = new HashMap<>();
    
    static {
        for (int i = 1; i < Bytecode.instructions.length; i++) {
            String key = Bytecode.instructions[i].name;
            Integer numArgs = Bytecode.instructions[i].n;
            byteByName.put(key, i);
            numArgsByName.put(key, numArgs);
        }
    }

    public static List<Integer> processLine(String line) {
        line = line.replaceAll("//.*", "") // remove comments
                   .replaceAll(",", "")    // remove commas
                   .toLowerCase();         // lower-case to search
        List<String> lineArgs = Arrays.asList(line.split("\\s+"));
        String opname = lineArgs.get(0);
        List<Integer> opArgs = new ArrayList<Integer>();
        opArgs.add(byteByName.get(opname));
        opArgs.addAll(lineArgs.subList(1, lineArgs.size())
                              .stream()
                              .map(Integer::parseInt)
                              .collect(Collectors.toList()));
        Integer listLength = 1 + numArgsByName.get(opname);
        return opArgs.subList(0, listLength);
    }

    public static void main(String[] args) throws Exception {
        String filename = args[0];
        List<String> lines = Files.lines(Paths.get(filename)).collect(Collectors.toList());
        List<Integer> bytecodes = lines.stream()
                                       .map(Runner::processLine)
                                       .flatMap(List::stream)
                                       .collect(Collectors.toList());
        int[] bytecodesArray = new int[bytecodes.size()];
        for (int i = 0; i < bytecodes.size(); i++) { bytecodesArray[i] = bytecodes.get(i); }
        VM vm = new VM(bytecodesArray, 0, 0);
        vm.trace = true;
        vm.exec();
    }

}
