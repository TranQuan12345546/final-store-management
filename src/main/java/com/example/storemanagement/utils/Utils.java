package com.example.storemanagement.utils;

import com.example.storemanagement.enity.Staff;
import com.example.storemanagement.enity.Store;
import com.example.storemanagement.enity.User;
import com.example.storemanagement.exception.NotFoundException;
import com.example.storemanagement.repository.StaffRepository;
import com.example.storemanagement.repository.StoreRepository;
import com.example.storemanagement.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class Utils {
    public static List<Integer> createList(Integer size) {
        List<Integer> range = IntStream.rangeClosed(1, size)
                .boxed().toList();

        return range;
    }

    static int num;
    public static int randomInt(int number) {
        Random rd = new Random();
        int nextNum = rd.nextInt(number) + 1;
        while (nextNum == num) {
            nextNum = rd.nextInt(number) + 1;
        }
        num = nextNum;
        return nextNum;
    }
}
