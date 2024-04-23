/*
 * @ (#) SanPham_Dao_Test.java    1.0    22/04/2024
 * Copyright (c) 2024 IUH. All rights reserved.
 */
package TestSanPham;/*
 * @description:
 * @author: Bao Thong
 * @date: 22/04/2024
 * @version: 1.0
 */

import bus.SanPham_Bus;
import dao.SanPham_Dao;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SanPham_Dao_Test {
    private SanPham_Bus sanPham_dao;

    @BeforeAll
    void setUp() {
        try {
            sanPham_dao = new SanPham_Dao();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    void testGetAllSanPham() {
        try {
            sanPham_dao.getAllSanPham().forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    void tearDown() {
        sanPham_dao = null;
    }
}
