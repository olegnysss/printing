<template>
    <v-container>
        <v-content>
            <h4 class="chatsHeader">Мои диалоги</h4>
            <ul class="list-group">
                <li class="list-group-item"
                    v-for="(chat,index) in chats"
                    :class="{ active: index == currentIndex }"
                    :key="index"
                >
                    <v-list-item three-line>
                        <v-list-item-content>
                            <v-list-item-title><strong>Собеседник: {{chat.companion}}</strong></v-list-item-title>
                            <v-list-item-subtitle>
                                <div v-if="chat.isExecutor">
                                    <strong>Ваша роль: {{"Исполнитель"}}</strong>
                                </div>
                                <div v-else>
                                    <strong>Ваша роль: {{"Заказчик"}}</strong>
                                </div>
                            </v-list-item-subtitle>
                            <v-list-item-subtitle>
                                <b-button class="mt-3" pill variant="primary" @click="goToChat(chat)">
                                    Перейти к диалогу
                                </b-button>
                            </v-list-item-subtitle>
                        </v-list-item-content>
                    </v-list-item>
                </li>
            </ul>
        </v-content>
    </v-container>
</template>

<script>
    import {AXIOS} from "../http-common";

    export default {
        props: [],
        data() {
            return {
                chats: [],
                currentIndex: -1,
            }
        },
        created: function () {
            AXIOS.get('/chat').then((response) => {
                this.chats = response.data;
                console.log(response.data);
            }).catch(error => console.log(error));
        },
        methods: {
            goToChat(chat) {
                var s = "/chat/" + chat.chatId;
                localStorage.setItem('currentChat', JSON.stringify(chat));
                this.$router.push(s)
            }
        }
    }
</script>

<style scoped>
.list-group-item{
    width: 1052px;
    margin-bottom: 12px;
    border-radius: 16px;
}

.list-group-item:first-child {
    border-top-left-radius: 16px;
    border-top-right-radius: 16px;
}

.v-card v-sheet theme--light{
    border-radius: 11px;
}

    .list-group-item:last-child {
        border-bottom-right-radius: 1.25rem;
        border-bottom-left-radius: 1.25rem;
    }
    .chatsHeader{
       margin-top: 6px;
       margin-left: 43px;
    }
</style>