import type { NotificationDto } from "../../api/__generated__";

const DUMMY_NOTIFICATIONS: NotificationDto[] = [
  {
    id: 1,
    senderId: 101,
    receiverId: 100,
    type: "LIKE",
    message: "김민수님이 회원님의 게시물을 좋아합니다.",
    createdAt: new Date(Date.now() - 1000 * 60 * 5), // 5 minutes ago
    read: false,
    deleted: false,
  },
  {
    id: 2,
    senderId: 102,
    receiverId: 100,
    type: "REQUEST",
    message: "이지은님이 친구 요청을 보냈습니다.",
    createdAt: new Date(Date.now() - 1000 * 60 * 30), // 30 minutes ago
    read: false,
    deleted: false,
  },
  {
    id: 3,
    senderId: 103,
    receiverId: 100,
    type: "LIKE",
    message: "박지훈님이 회원님의 댓글을 좋아합니다.",
    createdAt: new Date(Date.now() - 1000 * 60 * 60 * 2), // 2 hours ago
    read: true,
    deleted: false,
  },
  {
    id: 4,
    senderId: 104,
    receiverId: 100,
    type: "BLOCK",
    message: "시스템: 부적절한 콘텐츠로 인해 게시물이 삭제되었습니다.",
    createdAt: new Date(Date.now() - 1000 * 60 * 60 * 24), // 1 day ago
    read: true,
    deleted: false,
  },
  {
    id: 5,
    senderId: 105,
    receiverId: 100,
    type: "REQUEST",
    message: "최윤아님이 친구 요청을 보냈습니다.",
    createdAt: new Date(Date.now() - 1000 * 60 * 60 * 24 * 2), // 2 days ago
    read: true,
    deleted: false,
  },
  {
    id: 6,
    senderId: 106,
    receiverId: 100,
    type: "LIKE",
    message: "강동원님이 회원님의 게시물을 좋아합니다.",
    createdAt: new Date(Date.now() - 1000 * 60 * 60 * 24 * 3), // 3 days ago
    read: false,
    deleted: false,
  },
  {
    id: 7,
    senderId: 107,
    receiverId: 100,
    type: "BLOCK",
    message: "시스템: 보안 알림 - 새로운 기기에서 로그인이 감지되었습니다.",
    createdAt: new Date(Date.now() - 1000 * 60 * 60 * 24 * 4), // 4 days ago
    read: false,
    deleted: false,
  },
];

export default DUMMY_NOTIFICATIONS;
