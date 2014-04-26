//
//  Player.h
//  Lord of the Ping
//
//  Created by Matthew Runo on 4/25/14.
//  Copyright (c) 2014 Lord of the Ping. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Player : NSObject

@property (nonatomic, retain) NSString *id;
@property (nonatomic, retain) NSString *email;
@property (nonatomic, retain) NSString *password;
@property (nonatomic, retain) NSString *name;
@property (nonatomic, retain) NSString *avatarUrl;

@end
