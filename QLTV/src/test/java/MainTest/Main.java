/*
 * @ (#) Main.java    1.0    17/04/2024
 * Copyright (c) 2024 IUH. All rights reserved.
 */
package MainTest;/*
 * @description:
 * @author: Bao Thong
 * @date: 17/04/2024
 * @version: 1.0
 */

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("JPA_MSSQL");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        tx.commit();
    }
}
