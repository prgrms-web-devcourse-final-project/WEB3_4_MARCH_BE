import type { NotificationDto } from "../../api/__generated__";
import { ArrowLeft, Bell, Heart, MessageSquare, X } from "lucide-react";
import { formatDistanceToNow } from "../../utils/date";

export const NotificationListView = ({
  notifications,
  loading,
  onMarkAsReadNotification,
  onDeleteNotification,
}: {
  notifications: NotificationDto[];
  loading: boolean;
  onMarkAsReadNotification: (notificationId: number) => void;
  onDeleteNotification: (notificationId: number) => void;
}) => {
  const markAsRead = (id: number) => {
    onMarkAsReadNotification(id);
  };

  const deleteNotification = (id: number) => {
    onDeleteNotification(id);
  };

  const formatTimestamp = (timestamp: Date) => {
    try {
      return formatDistanceToNow(timestamp);
    } catch (error) {
      return "알 수 없음";
    }
  };

  // Get icon based on notification type
  const getNotificationIcon = (type: NotificationDto["type"]) => {
    switch (type) {
      case "LIKE":
        return <Heart size={20} className="text-red-500" />;
      case "REQUEST":
        return <MessageSquare size={20} className="text-blue-500" />;
      case "BLOCK":
        return <Bell size={20} className="text-gray-500" />;
      default:
        return <Bell size={20} className="text-gray-500" />;
    }
  };

  if (notifications.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center h-full p-4 text-center">
        <Bell size={40} className="text-gray-300 mb-3" />
        <p className="text-gray-500">알림이 없습니다</p>
      </div>
    );
  }

  return (
    <div className="h-full flex flex-col bg-white">
      <div className="flex-1 overflow-auto">
        <ul className="divide-y divide-gray-100">
          {notifications.map((notification) => (
            <li
              key={notification.id}
              className={`relative ${notification.read ? "bg-white" : "bg-gray-50"} h-[72px] w-full flex items-center justify-between`}
              onClick={() => {
                if (!notification.id) return;
                markAsRead(notification.id);
              }}
            >
              <div className="w-full h-full px-4 py-3 flex items-start">
                {/* Icon or user image */}
                <div className="flex-shrink-0 mr-3">
                  <div className="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center">
                    {getNotificationIcon(notification.type)}
                  </div>
                </div>

                {/* Content */}
                <div className="flex-1 min-w-0">
                  <div className="flex items-baseline justify-between">
                    <p className="text-sm font-medium text-gray-900 line-clamp-2">
                      <span className="font-normal">
                        {notification.message}
                      </span>
                    </p>
                    {notification.createdAt && (
                      <span className="text-xs text-gray-500 whitespace-nowrap ml-2">
                        {formatTimestamp(notification.createdAt)}
                      </span>
                    )}
                  </div>
                </div>

                {/* Delete button */}
                <button
                  className="ml-2 p-1 text-gray-400 hover:text-gray-600"
                  onClick={(e) => {
                    e.stopPropagation();
                    if (notification.id) {
                      deleteNotification(notification.id);
                    }
                  }}
                >
                  <X size={16} />
                </button>
              </div>

              {/* Unread indicator */}
              {!notification.read && (
                <div className="absolute left-0 top-0 bottom-0 w-1 bg-gray-400"></div>
              )}
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};
