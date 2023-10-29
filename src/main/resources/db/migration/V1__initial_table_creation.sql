CREATE TABLE IF NOT EXISTS users (
    id uuid NOT NULL,
    username varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    first_name varchar(255) NOT NULL,
    last_name varchar(255) NOT NULL,
    email varchar(255),
    CONSTRAINT PK_users_id PRIMARY KEY (id),
    CONSTRAINT UK_users_username UNIQUE (username),
    CONSTRAINT UK_users_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS suppliers (
    id uuid NOT NULL,
    supplier_name varchar(255) NOT NULL,
    phone_number varchar(255) NOT NULL,
    user_id uuid NOT NULL,
    CONSTRAINT PK_suppliers_id PRIMARY KEY (id),
    CONSTRAINT UK_suppliers_phone_number UNIQUE (phone_number),
    CONSTRAINT FK_suppliers_users_user_id FOREIGN KEY (user_id) REFERENCES "public".users (id)
);

CREATE TABLE IF NOT EXISTS invoices (
    id int NOT NULL,
    invoice_number varchar(255) NOT NULL,
    value bigint NOT NULL,
    currency varchar(255) NOT NULL,
    due_date date NOT NULL,
    introduction_date timestamp NOT NULL,
    supplier_id uuid NOT NULL,
    user_id uuid NOT NULL,
    status varchar(50) NOT NULL,
    invoice_image bytea,
    CONSTRAINT PK_invoices_id PRIMARY KEY (id, invoice_number),
    CONSTRAINT UK_invoices_invoice_number UNIQUE (invoice_number),
    CONSTRAINT UK_invoices_invoice_image UNIQUE (invoice_image),
    CONSTRAINT FK_invoices_suppliers_supplier_id FOREIGN KEY (supplier_id) REFERENCES "public".suppliers (id),
    CONSTRAINT FK_invoices_users_user_id FOREIGN KEY (user_id) REFERENCES "public".users (id)
);