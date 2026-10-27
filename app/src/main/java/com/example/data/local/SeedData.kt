package com.example.data.local

import com.example.data.model.Note
import com.example.data.model.NotificationItem
import com.example.data.model.User

object SeedData {
  val studentUser = User(
    id = "usr_student_01",
    name = "Alex Vance",
    email = "alex.vance@campus.edu",
    college = "Neo Institute of Technology",
    branch = "Computer Engineering",
    semester = "Semester 6",
    role = "STUDENT",
    avatarUrl = ""
  )

  val teacherUser = User(
    id = "usr_teacher_01",
    name = "Dr. Marcus Reed",
    email = "teacher@neonotes.edu",
    college = "Neo Institute of Technology",
    branch = "Computer Engineering",
    semester = "Faculty / Dept Head",
    role = "TEACHER",
    avatarUrl = ""
  )

  val initialUsers = listOf(
    studentUser,
    teacherUser,
    User(
      id = "usr_teacher_02",
      name = "Prof. Elena Rostova",
      email = "elena.r@neonotes.edu",
      college = "Stanford Institute of Tech",
      branch = "Information Technology",
      semester = "Faculty / Prof",
      role = "TEACHER"
    ),
    User(
      id = "usr_student_02",
      name = "Kaito Tanaka",
      email = "kaito.t@campus.edu",
      college = "Tokyo Tech Academy",
      branch = "Electronics",
      semester = "Semester 8",
      role = "STUDENT"
    )
  )

  val initialNotes = listOf(
    Note(
      id = "note_dsa_01",
      userId = "usr_teacher_01",
      uploaderName = "Dr. Marcus Reed",
      title = "Advanced Data Structures & Graph Algorithms",
      description = "Comprehensive lecture slides on AVL Trees, Red-Black Trees, Dijkstra, Bellman-Ford, and Network Flow algorithms with visual diagrams and proofs.",
      fileName = "DSA_Graph_Algorithms_Complete.pdf",
      fileUrl = "https://nitroflare.com/view/3A3E83E0EF1EC21/DSA_Graph_Algorithms_Complete.pdf",
      nitroflareUrl = "https://nitroflare.com/view/3A3E83E0EF1EC21/DSA_Graph_Algorithms_Complete.pdf",
      nitroflareXxhash = "20eb9cb44764aaa0",
      fileType = "PDF",
      fileSize = "4.8 MB",
      fileSizeBytes = 5033164L,
      subject = "Data Structures",
      branch = "Computer Engineering",
      semester = "Semester 4",
      university = "Neo Institute of Technology",
      academicYear = "2025-2026",
      tags = "DSA, Graphs, Trees, Algorithms, Lecture Deck",
      status = "APPROVED",
      downloadCount = 342,
      bookmarkCount = 89,
      lastAccessedAt = System.currentTimeMillis() - 86400000L * 1,
      createdAt = System.currentTimeMillis() - 86400000L * 4,
      updatedAt = System.currentTimeMillis() - 86400000L * 1
    ),
    Note(
      id = "note_dbms_02",
      userId = "usr_teacher_02",
      uploaderName = "Prof. Elena Rostova",
      title = "DBMS Normalization & ACID Transactions Architecture",
      description = "Comprehensive guide covering 1NF through BCNF, concurrency control, 2-phase locking, Write-Ahead Logging (WAL), and distributed query optimization.",
      fileName = "DBMS_Transactions_Normalization.pdf",
      fileUrl = "https://nitroflare.com/view/7F2D18B99AE1204/DBMS_Transactions_Normalization.pdf",
      nitroflareUrl = "https://nitroflare.com/view/7F2D18B99AE1204/DBMS_Transactions_Normalization.pdf",
      nitroflareXxhash = "59af3102c918ee4b",
      fileType = "PDF",
      fileSize = "3.2 MB",
      fileSizeBytes = 3355443L,
      subject = "Database Management Systems",
      branch = "Information Technology",
      semester = "Semester 5",
      university = "Stanford Institute of Tech",
      academicYear = "2025-2026",
      tags = "DBMS, SQL, Normalization, ACID, Concurrency",
      status = "APPROVED",
      downloadCount = 512,
      bookmarkCount = 143,
      lastAccessedAt = System.currentTimeMillis() - 86400000L * 2,
      createdAt = System.currentTimeMillis() - 86400000L * 7,
      updatedAt = System.currentTimeMillis() - 86400000L * 2
    ),
    Note(
      id = "note_os_03",
      userId = "usr_student_01",
      uploaderName = "Alex Vance",
      title = "Operating Systems Kernel & Virtual Memory Architecture",
      description = "Detailed notes on CPU scheduling heuristics, virtual memory paging, translation lookaside buffer (TLB), page faults, deadlock prevention, and Linux VFS internals.",
      fileName = "OS_Kernel_Virtual_Memory.docx",
      fileUrl = "https://nitroflare.com/view/14C5D9F88A11029/OS_Kernel_Virtual_Memory.docx",
      nitroflareUrl = "https://nitroflare.com/view/14C5D9F88A11029/OS_Kernel_Virtual_Memory.docx",
      nitroflareXxhash = "88b17ce41029aa87",
      fileType = "DOCX",
      fileSize = "2.1 MB",
      fileSizeBytes = 2202009L,
      subject = "Operating Systems",
      branch = "Computer Engineering",
      semester = "Semester 5",
      university = "Neo Institute of Technology",
      academicYear = "2025-2026",
      tags = "OS, Kernel, Memory, Scheduling, Deadlocks",
      status = "APPROVED",
      downloadCount = 289,
      bookmarkCount = 67,
      lastAccessedAt = System.currentTimeMillis() - 86400000L * 3,
      createdAt = System.currentTimeMillis() - 86400000L * 10,
      updatedAt = System.currentTimeMillis() - 86400000L * 3
    ),
    Note(
      id = "note_ai_04",
      userId = "usr_teacher_01",
      uploaderName = "Dr. Marcus Reed",
      title = "Deep Learning & Neural Network Fundamentals",
      description = "Lecture slide deck and revision annotations on backpropagation mathematical proofs, CNN architectures (ResNet, VGG), Attention mechanisms, and Transformer encoders.",
      fileName = "Deep_Learning_Neural_Nets.pptx",
      fileUrl = "https://nitroflare.com/view/9A8B7C6D5E4F312/Deep_Learning_Neural_Nets.pptx",
      nitroflareUrl = "https://nitroflare.com/view/9A8B7C6D5E4F312/Deep_Learning_Neural_Nets.pptx",
      nitroflareXxhash = "90aee412356bbff0",
      fileType = "PPTX",
      fileSize = "8.4 MB",
      fileSizeBytes = 8808038L,
      subject = "Artificial Intelligence",
      branch = "Computer Engineering",
      semester = "Semester 7",
      university = "Neo Institute of Technology",
      academicYear = "2025-2026",
      tags = "AI, Deep Learning, CNN, Attention, Transformers",
      status = "APPROVED",
      downloadCount = 670,
      bookmarkCount = 210,
      lastAccessedAt = System.currentTimeMillis() - 86400000L * 5,
      createdAt = System.currentTimeMillis() - 86400000L * 12,
      updatedAt = System.currentTimeMillis() - 86400000L * 5
    ),
    Note(
      id = "note_java_05",
      userId = "usr_teacher_02",
      uploaderName = "Prof. Elena Rostova",
      title = "Modern Java Enterprise & Spring Boot Microservices",
      description = "Practical guide to Spring Cloud, Dependency Injection, Hibernate JPA caching layers, Kafka event streaming, and JWT stateless security filters.",
      fileName = "Java_SpringBoot_Microservices.pdf",
      fileUrl = "https://nitroflare.com/view/4F3E2D1C0B9A887/Java_SpringBoot_Microservices.pdf",
      nitroflareUrl = "https://nitroflare.com/view/4F3E2D1C0B9A887/Java_SpringBoot_Microservices.pdf",
      nitroflareXxhash = "44d21ee90184ca78",
      fileType = "PDF",
      fileSize = "5.6 MB",
      fileSizeBytes = 5872025L,
      subject = "Java",
      branch = "Information Technology",
      semester = "Semester 6",
      university = "Stanford Institute of Tech",
      academicYear = "2025-2026",
      tags = "Java, Spring, Microservices, JPA, Cloud",
      status = "APPROVED",
      downloadCount = 415,
      bookmarkCount = 104,
      lastAccessedAt = System.currentTimeMillis() - 86400000L * 2,
      createdAt = System.currentTimeMillis() - 86400000L * 15,
      updatedAt = System.currentTimeMillis() - 86400000L * 2
    ),
    Note(
      id = "note_cn_06",
      userId = "usr_student_01",
      uploaderName = "Alex Vance",
      title = "Computer Networks: TCP Congestion & DNS Handshake",
      description = "Deep dive into OSI 7-layer stack, TCP Tahoe vs Reno congestion windows, BGP routing, TLS 1.3 handshake, and IPv6 subnetting formulas.",
      fileName = "Computer_Networks_Protocols.pdf",
      fileUrl = "https://nitroflare.com/view/5566778899AABB0/Computer_Networks_Protocols.pdf",
      nitroflareUrl = "https://nitroflare.com/view/5566778899AABB0/Computer_Networks_Protocols.pdf",
      nitroflareXxhash = "77cd104928bba331",
      fileType = "PDF",
      fileSize = "3.9 MB",
      fileSizeBytes = 4089446L,
      subject = "Computer Networks",
      branch = "Information Technology",
      semester = "Semester 5",
      university = "Neo Institute of Technology",
      academicYear = "2025-2026",
      tags = "Networks, TCP, IP, DNS, Protocols",
      status = "APPROVED",
      downloadCount = 378,
      bookmarkCount = 95,
      lastAccessedAt = System.currentTimeMillis() - 86400000L * 6,
      createdAt = System.currentTimeMillis() - 86400000L * 18,
      updatedAt = System.currentTimeMillis() - 86400000L * 6
    ),
    Note(
      id = "note_web_07",
      userId = "usr_student_01",
      uploaderName = "Alex Vance",
      title = "Full-Stack Web Architectures & React Concurrent Engine",
      description = "Notes detailing browser critical rendering path, React 19 Fiber reconciliation, WebSockets, SSR hydration, and Redis session stores.",
      fileName = "FullStack_Web_Architecture.pdf",
      fileUrl = "https://nitroflare.com/view/112233445566778/FullStack_Web_Architecture.pdf",
      nitroflareUrl = "https://nitroflare.com/view/112233445566778/FullStack_Web_Architecture.pdf",
      nitroflareXxhash = "66ee904817aa0922",
      fileType = "PDF",
      fileSize = "2.8 MB",
      fileSizeBytes = 2936012L,
      subject = "Web Development",
      branch = "Information Technology",
      semester = "Semester 4",
      university = "Neo Institute of Technology",
      academicYear = "2025-2026",
      tags = "Web, React, TypeScript, APIs, Performance",
      status = "APPROVED",
      downloadCount = 590,
      bookmarkCount = 188,
      lastAccessedAt = System.currentTimeMillis() - 86400000L * 1,
      createdAt = System.currentTimeMillis() - 86400000L * 2,
      updatedAt = System.currentTimeMillis() - 86400000L * 1
    ),
    // PENDING NOTES FOR APPROVAL TESTING
    Note(
      id = "note_pending_08",
      userId = "usr_student_02",
      uploaderName = "Kaito Tanaka",
      title = "Machine Learning Optimization & Convex Loss Landscapes",
      description = "Analytical solutions for gradient descent variations, AdamW weight decay derivations, Hessian matrices, and regularization proofs.",
      fileName = "ML_Convex_Optimization.pdf",
      fileUrl = "https://nitroflare.com/view/998877665544332/ML_Convex_Optimization.pdf",
      nitroflareUrl = "https://nitroflare.com/view/998877665544332/ML_Convex_Optimization.pdf",
      nitroflareXxhash = "11ab33cd55ef77aa",
      fileType = "PDF",
      fileSize = "3.7 MB",
      fileSizeBytes = 3879731L,
      subject = "Machine Learning",
      branch = "Computer Engineering",
      semester = "Semester 6",
      university = "Tokyo Tech Academy",
      academicYear = "2025-2026",
      tags = "ML, Optimization, Math, Gradient Descent",
      status = "PENDING",
      downloadCount = 0,
      bookmarkCount = 0,
      lastAccessedAt = null,
      createdAt = System.currentTimeMillis() - 3600000L * 3,
      updatedAt = System.currentTimeMillis() - 3600000L * 3
    )
  )

  val initialNotifications = listOf(
    NotificationItem(
      id = "notif_01",
      userId = "usr_student_01",
      title = "Note Approved!",
      message = "Your notes on 'Computer Networks' have been approved by the department and are now live on NitroFlare cloud storage.",
      type = "APPROVAL",
      isRead = false,
      createdAt = System.currentTimeMillis() - 3600000L * 4
    ),
    NotificationItem(
      id = "notif_02",
      userId = "usr_student_01",
      title = "Download Milestone Reached",
      message = "Congratulations! Your study material 'Full-Stack Web Architectures' surpassed 500 downloads on NitroFlare.",
      type = "MILESTONE",
      isRead = false,
      createdAt = System.currentTimeMillis() - 86400000L * 1
    ),
    NotificationItem(
      id = "notif_03",
      userId = "ALL",
      title = "Neo Notes v3.1 Released",
      message = "Welcome to Neo Notes! Choose your preferred theme: Neon Core, Obsidian Gold, or Sakura Mode.",
      type = "SYSTEM",
      isRead = true,
      createdAt = System.currentTimeMillis() - 86400000L * 3
    )
  )

  val subjectsList = listOf(
    "Data Structures",
    "Java",
    "Database Management Systems",
    "Operating Systems",
    "Computer Networks",
    "Web Development",
    "Artificial Intelligence",
    "Machine Learning",
    "Software Engineering",
    "Mathematics"
  )

  val branchesList = listOf(
    "Computer Engineering",
    "Information Technology",
    "Electronics",
    "Mechanical",
    "Civil"
  )

  val semestersList = listOf(
    "Semester 1",
    "Semester 2",
    "Semester 3",
    "Semester 4",
    "Semester 5",
    "Semester 6",
    "Semester 7",
    "Semester 8"
  )

  val academicYearsList = listOf(
    "2026-2027",
    "2025-2026",
    "2024-2025",
    "2023-2024"
  )
}
