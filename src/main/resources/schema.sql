create table if not exists Ingredient (
  id varchar(4) not null,
  name varchar(25) not null,
  type varchar(10) not null
);

create table if not exists Taco (
  id identity,
  name varchar(50) not null,
  createdAt timestamp not null
);

create table if not exists Taco_Ingredients (
  taco bigint not null,
  ingredient varchar(4) not null
);

create table if not exists Users (
    id identity,
    username varchar(50) not null,
    password varchar(255) not null,
    fullname varchar(255) not null,
    street varchar(50) not null,
	CITY varchar(50) not null,
	state varchar(12) not null,
	zip varchar(6) not null,
	phoneNumber varchar(16) not null
);

create table if not exists UserAuthorities (
    username varchar(50) not null,
    authority varchar(20) not null
);

alter table Taco_Ingredients
    add foreign key (taco) references Taco(id);
alter table Taco_Ingredients
    add foreign key (ingredient) references Ingredient(id);

create table if not exists Taco_Order (
	id identity,
	name varchar(50) not null,
	street varchar(50) not null,
	city varchar(50) not null,
	state varchar(12) not null,
	zip varchar(6) not null,
	ccNumber varchar(16) not null,
	ccExpiration varchar(5) not null,
	ccCVV varchar(3) not null,
	user bigint not null,
    placedAt timestamp not null
);

create table if not exists Taco_Order_Tacos (
	tacoOrder bigint not null,
	taco bigint not null
);

alter table Taco_Order_Tacos
    add foreign key (tacoOrder) references Taco_Order(id);
alter table Taco_Order_Tacos
    add foreign key (taco) references Taco(id);