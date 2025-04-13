import { Loading } from "../components/Loading";
import { useUserStore } from "../features/auth/useUserStore";
import DUMMY_NOTIFICATIONS from "../features/notifications/dumy";
import { NotificationListView } from "../features/notifications/NotificationListView";
import {
  useNotificationDelete,
  useNotificationRead,
} from "../features/notifications/useNotificationMutations";
import { useNotifications } from "../features/notifications/useNotifications";
import AppScreenLayout from "../layout/AppScreenLayout";

export const NotificationActivity = () => {
  const { profile } = useUserStore((s) => ({
    profile: s.profile,
  }));

  // const { notifications, isLoading, isError } = useNotifications({
  //   memberId: profile?.id,
  // });

  const notifications = DUMMY_NOTIFICATIONS;
  const isLoading = false;
  const isError = false;

  const { markAsRead } = useNotificationRead();
  const { deleteNotification } = useNotificationDelete();

  const handleMarkAsRead = (notificationId: number) => {
    if (!profile?.id) return;

    markAsRead({
      memberId: profile?.id,
      notificationId,
    });
  };

  const handleDeleteNotification = (notificationId: number) => {
    if (!profile?.id) return;

    deleteNotification({
      memberId: profile?.id,
      notificationId,
    });
  };

  return (
    <AppScreenLayout title="알림" wideScreen>
      {isLoading && (
        <div className="h-full w-full flex flex-col items-center justify-center">
          <Loading text="알림을 불러오고 있습니다..." />
        </div>
      )}
      {isError && <div>알림을 불러오는데 실패했습니다.</div>}
      {!isLoading && !isError && (
        <NotificationListView
          notifications={notifications ?? []}
          loading={isLoading}
          onMarkAsReadNotification={handleMarkAsRead}
          onDeleteNotification={handleDeleteNotification}
        />
      )}
    </AppScreenLayout>
  );
};
