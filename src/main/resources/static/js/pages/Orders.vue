<template>
    <v-container>
        <div v-if="currentExecutor">
            <b-alert show dismissible fade>
                Вы выбираете заказ для исполнителя {{currentExecutorName}}<br>
                <b-button class="mt-3" variant="outline-danger" @click="clearExecutor">
                    Очистить
                </b-button>
            </b-alert>
        </div>
        <div v-if="!currentOrder">
            <b-alert show dismissible fade>Выберите заказ.</b-alert>

        </div>

        <v-content>
            <v-row align="center"
                   justify="center">
                <div class="col-md-8">
                    <div class="input-group mb-5">
                        <input type="text" class="form-control" placeholder="Имя заказа" v-model="orderName"/>
                        <div class="input-group-append">
                            <button class="btn btn-outline-secondary" type="button" @click="findOrders">
                                <v-icon left>mdi-magnify</v-icon>
                                Найти
                            </button>
                            <button class="btn btn-outline-secondary" type="button" @click="showAll"> Показать Всех
                            </button>
                        </div>
                    </div>
                </div>
            </v-row>
        </v-content>

        <v-card max-width="1100">
            <v-card-title>
                <h4 align="center" class="ordersHeader">Заказы</h4>
                <v-spacer></v-spacer>
                <v-btn class="mx-2" dark small color="indigo" to="/order/create">
                    <v-icon dark>mdi-plus</v-icon>
                    Создать
                </v-btn>
            </v-card-title>
            <ul class="list-group" max-width="600">
                <li class="list-group-item"
                    v-for="(order,index) in orders"
                    :class="{ active: index == currentIndex }"
                    :key="index"
                    @click="setActiveOrder(order, index, order.status)"
                    @dblclick="goToOrder(order)"
                >

                    <v-list-item rounded  >
                        <v-list-item-content three-line>
                            <v-list-item-title>
                                <strong>Имя заказа: {{order.name}}</strong>
                            </v-list-item-title>
                            <v-list-item-subtitle>
                                <strong>Статус: {{order.status}}</strong>
                            </v-list-item-subtitle>
                            <v-list-item-subtitle>
                                <strong>Сумма: {{order.sum}}</strong>
                            </v-list-item-subtitle>
                            <v-list-item-subtitle>
                                <div>
                                    <strong>Материалы: <span
                                            v-for="material of order.materials"> {{material}}  </span></strong>
                                </div>
                            </v-list-item-subtitle>
                            <v-list-item-subtitle>
                                <strong>Получено {{order.responsesCount}} откликов </strong>
                            </v-list-item-subtitle>
                        </v-list-item-content>
                    </v-list-item>
                </li>
                <div v-if="pagination.need">
                    <v-pagination
                            v-model="pagination.page"
                            :length="pagination.total"
                            total-visible=6
                            @input="getOrders"
                    ></v-pagination>
                </div>

            </ul>

            <div v-if="currentOrder">
                <div class="mt-2"></div>
                <label><strong>Текущий заказ:</strong></label> {{ currentOrder.name }}
            </div>
        </v-card>

        <v-content>
            <div class="mt-2">
                <div v-if="currentStatus === 'Черновик'">
                    <b-button variant="danger" @click="showModal">Удалить заказ</b-button>

                    <b-modal ref="my-modal" hide-footer title="Подтверждение">
                        <div class="d-block text-center">
                            <h3>Удалить заказ?</h3>
                        </div>
                        <b-button class="mt-3" variant="outline-danger" block @click="deleteOrder">Удалить</b-button>
                    </b-modal>
                    <div class="mt-2"></div>
                    <b-alert show dismissible fade>Вы не можете назначать исполнителя для шаблона.</b-alert>


                </div>
                <div v-else-if="currentOrder">
                    <div v-if="!currentExecutor">
                        <v-btn class="red--text" @click="selectExecutor">Выбрать исполнителя для заказа</v-btn>
                    </div>
                    <div v-if="currentExecutor">
                        <b-button v-b-modal.modal-1>Предложить заказ исполнителю</b-button>

                        <b-modal
                                id="modal-1"
                                title="Введите подробности заказа"
                                @ok="sendResponse"
                        >
                            <h5>Стоимость заказа:</h5>
                            <b-form-input type="text" placeholder="Сумма заказа" v-model="sum"/>
                            <div class="mt-2"></div>
                        </b-modal>
                    </div>
                    <div class="mt-2"></div>
                    <v-btn class="blue-grey--text" @click="watchResponses"> Посмотреть отклики </v-btn>

                    <div class="mt-2"></div>
                    <b-button variant="danger" @click="showModal">Удалить заказ</b-button>

                    <b-modal ref="my-modal" hide-footer title="Подтверждение">
                        <div class="d-block text-center">
                            <h3>Удалить заказ?</h3>
                        </div>
                        <b-button class="mt-3" variant="outline-danger" block @click="deleteOrder">Удалить</b-button>
                    </b-modal>
                </div>

            </div>

        </v-content>
    </v-container>

</template>

<script>
    import {AXIOS} from "../pages/http-common";

    export default {
        props: [],
        data() {
            return {
                orders: [],
                currentOrder: null,
                currentIndex: -1,
                currentStatus: null,
                orderName: null,
                accessToken: localStorage.getItem('accessToken'),
                currentExecutor: localStorage.getItem('currentExecutor'),
                currentExecutorName: localStorage.getItem('currentExecutorName'),
                pagination: {
                    page: 1,
                    total: 0,
                    perPage: 4,
                    need: false
                }

            }
        },
        created: function () {
            this.getOrders();
        },
        methods: {
            getOrders() {
                AXIOS.get('/order/user?page=' + this.pagination.page +
                    '&perPage=' + this.pagination.perPage +
                    '&orderName=' + this.$data.orderName
                ).then((response) => {
                    this.orders = response.data.content;
                    this.pagination.total = response.data.pageCount;
                    if (this.pagination.total > 1) this.pagination.need = true;
                    console.log(response.data);
                }).catch(error => console.log(error));
            },
            showModal() {
                this.$refs['my-modal'].show()
            },
            setActiveOrder(order, index, status) {
                this.currentOrder = order;
                this.currentIndex = index;
                this.currentStatus = status
            },
            goToOrder(order) {
                var s = "/orders/" + order.id;
                this.$router.push(s)
            },
            selectExecutor() {
                localStorage.setItem('currentOrder', JSON.stringify(this.currentOrder));
                this.$router.push('/')
            },
            clearExecutor() {
                localStorage.removeItem('currentExecutor');
                location.reload();
            },
            sendResponse() {
                AXIOS.post("/chat", {
                    'executorId': this.currentExecutor,
                    'customerId': this.currentOrder.customerId
                }).then((response) => {
                    console.log(response);
                }).catch(error => console.log(error));

                AXIOS.post("/response", {
                    'orderId': this.currentOrder.id,
                    'executorId': localStorage.getItem('currentExecutor'),
                    'sum': this.sum
                }).then((response) => {
                    console.log(response);
                    localStorage.removeItem('currentExecutor');

                    this.$router.push('/chatList');
                    location.reload();
                }).catch(error => console.log(error));

            },
            deleteOrder() {
                AXIOS.delete('/order/' + this.currentOrder.id);
                location.reload()
            },
            watchResponses() {
                localStorage.setItem('order', JSON.stringify(this.currentOrder));
                this.$router.push('/responses/' + this.currentOrder.id);
            },
            findOrders() {
                this.pagination.need = false;
                this.pagination.page = 1;
                this.getOrders();
            },
            showAll() {
                this.pagination.page = 1;
                this.pagination.need = false;
                this.$data.orderName = null;
                this.getOrders();
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

.ordersHeader{
    margin-top: 6px;
    margin-left: 43px;}


    .v-application .indigo {
        background-color: #007bff!important;
        border-color: #007bff!important;
        border-radius: 23px;
    }
    .v-btn:not(.v-btn--round).v-size--small{
        height: 41px;
    }
    .v-btn.v-size--small{
        font-size: 0.9rem;
    }

    .list-group-item:last-child {
        border-bottom-right-radius: 1.25rem;
        border-bottom-left-radius: 1.25rem;
    }
</style>